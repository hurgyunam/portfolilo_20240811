import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:cosign_app/datas/enums/purchase_dialog_state.dart';
import 'package:cosign_app/datas/model/auth/purchase_result.dart';
import 'package:cosign_app/datas/model/auth/user_purchase_product.dart';
import 'package:cosign_app/elements/auth/purchase/purchase_dialog.dart';
import 'package:cosign_app/provider/non_state/purchase_logger.dart';
import 'package:cosign_app/provider/non_state/sercure_storage_provider.dart';
import 'package:cosign_app/provider/state/auth/user_ticket_info_provider.dart';
import 'package:cosign_app/repository/purchase_repository.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:in_app_purchase_android/in_app_purchase_android.dart';
import 'package:in_app_purchase_storekit/in_app_purchase_storekit.dart';
import 'package:in_app_purchase_storekit/store_kit_wrappers.dart';
import 'package:intl/intl.dart';

class CosignPurchaseStatus {
  PurchaseDialog? purchaseDialog;
  bool isLoading;
  bool isPurchaseSuccess;

  CosignPurchaseStatus({
    this.purchaseDialog,
    this.isLoading = false,
    this.isPurchaseSuccess = false,
  });
}

final purchaseProvider =
    StateNotifierProvider<PurchaseStateNotifier, CosignPurchaseStatus>((ref) {
  final purchaseRepository = ref.read(purchaseRepositoryProvider);
  final storage = ref.read(secureStorageProvider);
  final purchaseLogger = ref.watch(purchaseLoggerProvider);
  final userTickets = ref.watch(userTicketInfoProvider.notifier);

  return PurchaseStateNotifier(
    purchaseRepository: purchaseRepository,
    storage: storage,
    purchaseLogger: purchaseLogger,
    userTicketInfoNotifier: userTickets,
  );
});

class PurchaseStateNotifier extends StateNotifier<CosignPurchaseStatus> {
  final PurchaseRepository purchaseRepository;
  final SecureStorageManager storage;
  final UserTicketInfoNotifier userTicketInfoNotifier;
  final PurchaseLogger purchaseLogger;
  // String? lastPurchasedProductId;
  UserPurchaseProduct? userPurchaseProduct;
  StreamSubscription<List<PurchaseDetails>>? subscription;
  final DateFormat dateFormat = DateFormat('yyyy-MM-ddTHH:mm:ss');

  PurchaseStateNotifier({
    required this.purchaseRepository,
    required this.storage,
    required this.userTicketInfoNotifier,
    required this.purchaseLogger,
  }) : super(CosignPurchaseStatus());

  initStream() {
    subscription ??= InAppPurchase.instance.purchaseStream.listen((purchases) {
      _listen(purchases);
    }, onDone: () {
      subscription!.cancel();
    }, onError: (error) {
      subscription!.resume();
    });
  }

  closeStream() async {
    await subscription?.cancel();
    subscription = null;
  }

  Future<void> buyProduct({
    required ProductDetails productDetails,
    bool isSubscription = false,
  }) async {
    state = CosignPurchaseStatus(isLoading: true);

    final userUUID = await storage.getUserId();
    UserPurchaseProduct? userPurchaseProduct;
    PurchaseDialogState? purchaseDialogState;

    if (userUUID != null) {
      if (productDetails is GooglePlayProductDetails) {
        final passRes =
            await purchaseRepository.getGooglePlayStorePass(userUUID: userUUID);

        final serverId = passRes.data?.basePlanId ?? passRes.data?.productId;

        userPurchaseProduct = UserPurchaseProduct.createByPlayStoreProduct(
            product: productDetails, serverId: serverId);
      } else if (productDetails is AppStoreProductDetails) {
        final passRes =
            await purchaseRepository.getApplePlayStorePass(userUUID: userUUID);

        userPurchaseProduct = UserPurchaseProduct.createByAppStoreProduct(
          product: productDetails,
          serverId: passRes.data?.productId,
        );

        await Future(() async {
          final Completer completer = Completer();
          int? transactionCount;

          while (transactionCount != 0) {
            final transactions = await SKPaymentQueueWrapper().transactions();
            transactionCount = transactions.length;
            final purchasingCount = transactions
                .where((transaction) =>
                    transaction.transactionState ==
                    SKPaymentTransactionStateWrapper.purchasing)
                .toList()
                .length;

            if (purchasingCount > 0) {
              await Future.delayed(const Duration(seconds: 1));
            } else {
              for (var transaction in transactions) {
                await SKPaymentQueueWrapper().finishTransaction(transaction);
              }
            }
          }

          completer.complete(null);
        });
      }

      if (userPurchaseProduct != null) {
        purchaseDialogState =
            await userPurchaseProduct.checkPurchaseExceptionalCase();

        this.userPurchaseProduct = userPurchaseProduct;
      } else {
        purchaseDialogState = PurchaseDialogState.error;
      }
    } else {
      purchaseDialogState = PurchaseDialogState.error;
    }

    if (purchaseDialogState == null) {
      try {
        initStream();
        if (isSubscription) {
          final purchaseParam = PurchaseParam(productDetails: productDetails);
          await InAppPurchase.instance
              .buyNonConsumable(purchaseParam: purchaseParam);
        } else {
          final purchaseParam = PurchaseParam(productDetails: productDetails);
          await InAppPurchase.instance
              .buyConsumable(purchaseParam: purchaseParam);
        }
      } catch (e, stackTrace) {
        closeStream();

        state = CosignPurchaseStatus(
            purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error));

        purchaseLogger.logStackTrace(
            category: "BUY CONSUMABLE EXCEPTION", e: e, stackTrace: stackTrace);
      }
    } else {
      state = CosignPurchaseStatus(
          purchaseDialog: PurchaseDialog(state: purchaseDialogState));
    }
  }

  Future<void> _listen(List<PurchaseDetails> purchases) async {
    final userUUID = await storage.getUserId();
    final lastPurchased = _findLastPurchased(purchases);

    print("Grady: _listen ${lastPurchased?.status}");
    if (lastPurchased != null &&
        userUUID != null &&
        lastPurchased.status == PurchaseStatus.purchased) {
      InAppPurchase.instance.completePurchase(lastPurchased);
      await _sendReceipt(lastPurchased: lastPurchased, userUUID: userUUID);
      closeStream();
    } else {
      _checkExceptionalCase(lastPurchased);
    }
  }

  Future<void> _sendReceipt({
    required PurchaseDetails lastPurchased,
    required String userUUID,
  }) async {
    const retryLimit = 3;
    const waitDuration =
        Duration(seconds: 0); // 결제 retry 동안 dim 처리하는데 문제가 있어 임시 처리
    final inAppPurchaseJson =
        lastPurchased.verificationData.localVerificationData;

    int retryCount = 0;
    bool isTrySuccess = false;

    while (retryCount < retryLimit && isTrySuccess == false) {
      if (retryCount > 0 && isTrySuccess == false) {
        print("Grady: _sendReceipt $retryCount");
        await Future.delayed(waitDuration);
      }

      try {
        bool isSuccess = false;
        String errorJson = "undefined";
        final userPurchaseProduct = this.userPurchaseProduct;

        if (userPurchaseProduct != null) {
          if (userPurchaseProduct.isAndroidOneTime) {
            final googleResponse =
                await purchaseRepository.postAndroidGrantOneTime(
                    userUUID: userUUID,
                    inAppPurchaseJson: inAppPurchaseJson,
                    productId: userPurchaseProduct.selectedId);
            isSuccess = googleResponse.data != null;
            errorJson = jsonEncode(googleResponse.error);
          } else if (userPurchaseProduct.isAndroidSubscription) {
            final googleResponse =
                await purchaseRepository.postAndroidGrantSubscription(
                    userUUID: userUUID,
                    inAppPurchaseJson: inAppPurchaseJson,
                    productId: userPurchaseProduct.selectedId);
            isSuccess = googleResponse.data != null;
            errorJson = jsonEncode(googleResponse.error);
          } else if (userPurchaseProduct.isIOSOneTime) {
            final receipt = await findIOSLastReceipt();
            final res = await purchaseRepository.postIosGrantOneTime(
                userUUID: userUUID,
                transactionId: receipt.transactionId,
                originalTransactionId: receipt.originalTransactionId,
                productId: receipt.productId);

            isSuccess = res.data != null;
            errorJson = jsonEncode(res.error);
          } else if (userPurchaseProduct.isIOSSubscription) {
            final receipt = await findIOSLastReceipt();
            final res = await purchaseRepository.postIosGrantSubscription(
                userUUID: userUUID,
                transactionId: receipt.transactionId,
                originalTransactionId: receipt.originalTransactionId,
                productId: receipt.productId);

            isSuccess = res.data != null;
            errorJson = jsonEncode(res.error);
          }
        }

        if (isSuccess) {
          await purchaseLogger.logOrderResponse(
              category: "API 200", isSuccess: true, errorJson: errorJson);
          await userTicketInfoNotifier.getUserTicketInfo();

          isTrySuccess = true;
        } else {
          purchaseLogger.logRetry(
            category: "API NOT 200",
            isSuccess: false,
            retryCount: retryCount + 1,
            retryNumber: retryLimit,
            errorJson: errorJson,
          );

          isTrySuccess = false;
          retryCount++;
        }
      } catch (e, stackTrace) {
        purchaseLogger.logRetry(
            category: "UNKNOWN",
            retryCount: retryCount + 1,
            retryNumber: retryLimit,
            isSuccess: false,
            errorJson: e.toString());
        purchaseLogger.logStackTrace(
            category: "UNKNOWN", e: e, stackTrace: stackTrace);

        isTrySuccess = false;
        retryCount++;
      }
    }

    final category = (retryCount < retryLimit) ? "SUCCESS" : "RETRY FAILED";
    purchaseLogger.logRetry(
        category: category,
        isSuccess: (retryCount < retryLimit),
        retryCount: retryCount + 1,
        retryNumber: retryLimit,
        errorJson: "{}");

    if (isTrySuccess) {
      userTicketInfoNotifier.getUserTicketInfo();

      state = CosignPurchaseStatus(isPurchaseSuccess: true); // 일반적인 구매
    } else {
      state = CosignPurchaseStatus(
          purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error));
    }
  }

  Future<IosPurchaseReceipt> findIOSLastReceipt() async {
    int retryCount = 0, retryMaxCount = 3;
    IosPurchaseReceipt? receipt;

    while (receipt == null && retryCount < retryMaxCount) {
      const platform = MethodChannel("com.lab2ai.cosignapp.purchase");
      final results = await platform.invokeMethod("get"); // 배열임..

      if (results is List && results.isNotEmpty && results[0] is Map) {
        final receipts = results
            .map(
              (map) => IosPurchaseReceipt(
                  transactionId: map["transactionId"],
                  originalTransactionId: map["originalTransactionId"],
                  purchaseDate: map["purchaseDate"],
                  productId: map["productId"]),
            )
            .toList();

        DateFormat format = DateFormat("yyyy-MM-dd HH:mm:ss");
        final rangeDateText =
            format.format(DateTime.now().subtract(const Duration(minutes: 10)));
        receipts.sort((a, b) => b.purchaseDate.compareTo(a.purchaseDate));

        if (receipts[0].purchaseDate.compareTo(rangeDateText) > 0) {
          receipt = receipts[0];
        } else {
          print(
              "Grady: findIOSLastReceipt ${receipts[0].purchaseDate} ${receipts.length}");
        }
      }

      retryCount++;

      await Future.delayed(const Duration(seconds: 3));
    }

    if (receipt != null) {
      return receipt;
    } else {
      throw StateError("[COSIGN] cannot find receipt");
    }
  }

  PurchaseDetails? _findLastPurchased(List<PurchaseDetails> purchases) {
    final optionalLastPurchased = purchases
        .where(
            (element) => element.productID == userPurchaseProduct?.selectedId)
        .toList();
    PurchaseDetails? lastPurchased;

    if (optionalLastPurchased.length == 1) {
      lastPurchased = optionalLastPurchased[0];
    } else if (optionalLastPurchased.length > 1) {
      // 구독 구매일 경우 배열로 날라올 수도 있다.
      optionalLastPurchased.sort((a, b) {
        if (a.transactionDate != null && b.transactionDate != null) {
          return a.transactionDate!.compareTo(b.transactionDate!);
        } else {
          return 0;
        }
      });

      lastPurchased = optionalLastPurchased[0];
    } else if (purchases.length == 1 && Platform.isAndroid) {
      lastPurchased = purchases[0];
    }

    return lastPurchased;
  }

  void _checkExceptionalCase(PurchaseDetails? lastPurchased) {
    switch (lastPurchased?.status) {
      case PurchaseStatus.error:
        purchaseLogger.logIAPError(
          category: "ERROR",
          error: lastPurchased?.error,
          verificationData:
              lastPurchased?.verificationData.localVerificationData,
        );

        closeStream();

        state = CosignPurchaseStatus(
          purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error),
        );

        break;
      case PurchaseStatus.pending:
        state = CosignPurchaseStatus(isLoading: true);

        break;
      case PurchaseStatus.restored:
        purchaseLogger.logIds(
          category: "RESTORED",
          purchaseId: lastPurchased?.purchaseID,
          productId: userPurchaseProduct?.selectedId,
        );

        closeStream();

        state = CosignPurchaseStatus(
          isLoading: false,
          // purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error),
        );

        break;
      case PurchaseStatus.canceled:
        // state = CosignPurchaseStatus(
        //     purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error));
        // purchaseLogger.logIds(
        //   category: "CANCELED",
        //   purchaseId: lastPurchased?.purchaseID,
        //   productId: lastPurchasedProductId,
        // );

        InAppPurchase.instance.completePurchase(lastPurchased!);
        closeStream();
        state = CosignPurchaseStatus(
          isLoading: false,
        );
        break;
      default:
        closeStream();
        state = CosignPurchaseStatus(
          purchaseDialog: PurchaseDialog(state: PurchaseDialogState.error),
        );
        break;
    }
  }
}
