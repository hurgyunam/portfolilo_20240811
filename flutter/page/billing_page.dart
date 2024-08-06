import 'package:cosign_app/datas/enums/purchase_dialog_state.dart';
import 'package:cosign_app/datas/model/auth/match_product.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans.dart';
import 'package:cosign_app/elements/auth/purchase/purchase_dialog.dart';
import 'package:cosign_app/provider/non_state/analytics_provider.dart';
import 'package:cosign_app/provider/non_state/cosign_mixpanel_provider.dart';
import 'package:cosign_app/provider/state/auth/auth_provider.dart';
import 'package:cosign_app/provider/state/auth/cert_provider.dart';
import 'package:cosign_app/provider/state/auth/purchase_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:in_app_purchase/in_app_purchase.dart';

class BillingPage extends ConsumerStatefulWidget {
  const BillingPage({Key? key}) : super(key: key);

  @override
  _BillingPageState createState() => _BillingPageState();
}

class _BillingPageState extends ConsumerState<BillingPage> {
  MatchedProductViewData? matchedProductViewData;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    final analytics = ref.watch(analyticsProvider);
    final mixpanel = ref.watch(cosignMixpanelProvider);
    analytics.logEvent(screenName: "이용권구매");
    mixpanel.setEvent(eventName: '이용권구매');
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    matchTicketInfo().then((data) {
      setState(() {
        matchedProductViewData = data;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    ref.listen<CosignPurchaseStatus>(purchaseProvider, onChangePurchaseStatus);
    final purchaseResult = ref.watch(purchaseProvider);

    return Stack(
      children: [
        PricingPlans(
          matched: matchedProductViewData,
          onPurchase: (product) {
            onClickProductPurchaseButton(
                productDetails: product.productDetails,
                isSubscription: product.isSubscription);
          },
        ),
        if (purchaseResult.isLoading) ...[
          const Opacity(
              opacity: 0.8,
              child: ModalBarrier(
                dismissible: false,
                color: Colors.black,
              )),
          const Center(child: CircularProgressIndicator()),
        ]
      ],
    );
  }

  void onClickProductPurchaseButton({
    required ProductDetails productDetails,
    bool isSubscription = false,
  }) async {
    final isLogin = ref.read(authProvider);

    if (isLogin) {
      final certResult = await ref.read(certProvider.notifier).getCertResult();

      if (certResult == CertResult.majorAge) {
        final purchaseStateNotifier = ref.read(purchaseProvider.notifier);

        await purchaseStateNotifier.buyProduct(
            productDetails: productDetails, isSubscription: isSubscription);
      } else {
        final state = (certResult == CertResult.minorAge)
            ? PurchaseDialogState.minorAge
            : (certResult == CertResult.notCertified)
                ? PurchaseDialogState.notCertified
                : PurchaseDialogState.error;
        if (context.mounted) {
          final purchaseDialog = PurchaseDialog(state: state, context: context);

          showDialog(context: context, builder: purchaseDialog.build);
        }
      }
    } else {
      context.push('/login_main');
    }
  }

  void onChangePurchaseStatus(
      CosignPurchaseStatus? previous, CosignPurchaseStatus next) async {
    // purchaseProvider의 state변경 callback이며, state는 구매성공, 에러 등등이다.

    if (next.isPurchaseSuccess) {
      ref.read(cosignMixpanelProvider).setEvent(eventName: '이용권 구매 완료');
      Navigator.pop(context); // 구매가 완료되면 메뉴페이지로 빠지며 구매 상태가 보여진다
    } else if (next.purchaseDialog != null) {
      final purchaseDialog = next.purchaseDialog!;
      showDialog(context: context, builder: purchaseDialog.build);
    }
  }
}
