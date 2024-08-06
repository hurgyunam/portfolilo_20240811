import 'package:cosign_app/common/const/strings.dart';
import 'package:cosign_app/datas/enums/purchase_dialog_state.dart';
import 'package:cosign_app/elements/common/cosign_dialog.dart';
import 'package:cosign_app/page/auth/cert_screen.dart';
import 'package:cosign_app/page/common/popup_page.dart';
import 'package:flutter/material.dart';
import 'package:in_app_purchase/in_app_purchase.dart';

class PurchaseDialog {
  late CosignDialog _cosignDialog;

  PurchaseDialog({required PurchaseDialogState state, BuildContext? context}) {
    VoidCallback? onPressed;
    String message = "";
    bool isClosingEnd = false, isClosingStart = false;
    switch (state) {
      case PurchaseDialogState.error:
        message = textPurchaseError;
        isClosingEnd = true;
        onPressed = () {
          InAppPurchase.instance.restorePurchases();
        };
        break;
      case PurchaseDialogState.minorAge:
        message = textMinorAge;
        isClosingEnd = true;
        break;
      case PurchaseDialogState.notCertified:
        message = textNotCertified;
        isClosingStart = true;
        if (context != null) {
          onPressed = () {
            Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) => const PopupPage(
                          needLog: true,
                          pageName: '본인인증',
                          child: CertScreen(),
                        )));
          };
        }
        break;
      case PurchaseDialogState.itemAlreadyPurchased:
        message = textAlreadyPurchased;
        isClosingEnd = true;
        break;
      case PurchaseDialogState.itemAlreadyPurchased30days:
        message = textItemAlreadyPurchased30days;
        isClosingEnd = true;
        break;
      case PurchaseDialogState.itemAlreadyPurchasedSubscription:
        message = textItemAlreadyPurchasedSubscription;
        isClosingEnd = true;
        break;
      case PurchaseDialogState.itemDeprecatedProduct:
        message = textOldPurchase;
        isClosingEnd = true;
        break;
      // case PurchaseDialogState.iosUpgradeTrial:
      //   message = textIosSubscriptionUpgradeTrial;
      //   isClosingEnd = true;
      //   break;
      // case PurchaseDialogState.storeAccountSyncError: Store 결제 이력에서 basePlanId를 구분할 방법이 없다.
      //   message = textAnotherStoreAccountPurchased;
      //   isClosingEnd = true;
      //   break;
      // case PurchaseDialogState.anotherCosignAccountPurchased:
      //   message = textStoreOnlyPurchased;
      //   isClosingEnd = true;
      //   break;
    }

    _cosignDialog = CosignDialog(
      content: message,
      buttons: [
        CosignDialogButtonInfo(
          text: textOk,
          isMajor: true,
          isClosingStart: isClosingStart,
          isClosingEnd: isClosingEnd,
          onPressed: onPressed,
        )
      ],
    );
  }

  Widget build(BuildContext buildContext) {
    return _cosignDialog.build(buildContext);
  }
}
