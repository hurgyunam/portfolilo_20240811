import 'package:cosign_app/common/const/productId.dart';
import 'package:cosign_app/common/v2_design/index.dart';
import 'package:cosign_app/datas/model/auth/match_product.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans_footer.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans_item.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans_title.dart';
import 'package:cosign_app/page/common/popup_page.dart';
import 'package:flutter/material.dart';

class PricingPlans extends StatelessWidget {
  final MatchedProductViewData? matched;
  final void Function(ProductViewData) onPurchase;
  PricingPlans({required this.matched, required this.onPurchase});

  @override
  Widget build(BuildContext context) {
    final basicSubscription = matched?.basicSubscription;
    final premiumSubscription = matched?.premiumSubscription;
    final basicDay30 = matched?.basicDay30;
    final premiumDay30 = matched?.premiumDay30;

    return PopupPage(
      title: "이용권 구매",
      child: SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const PricingPlansTitle(),
            const SizedBox(
              height: 24,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: 16,
              ),
              child: Column(
                children: [
                  if (basicSubscription != null && basicDay30 != null)
                    PricingPlansItem(
                      description: "국내거래소 투자자에게 추천",
                      descriptionEmoji: "💁🏻‍♀️️ ",
                      buttonText: "베이직 이용권 구매",
                      subscription: basicSubscription,
                      day30: basicDay30,
                      color: V2Design.color.background.ticketPageGreen,
                      gradient: LinearGradient(colors: [
                        V2Design.color.background.ticketPageGreen,
                        V2Design.color.background.ticketPageGreen,
                      ]),
                      onButtonTap: (ProductViewData productViewData) {
                        onPurchase(productViewData);
                      },
                    ),
                  const SizedBox(
                    height: 24,
                  ),
                  if (premiumSubscription != null && premiumDay30 != null)
                    PricingPlansItem(
                      description: "해외거래소 투자자에게 추천",
                      descriptionEmoji: "💁🏼‍♀️ ",
                      buttonText: "프리미엄 이용권 구매",
                      subscription: premiumSubscription,
                      day30: premiumDay30,
                      color: V2Design.color.font.short,
                      gradient: const LinearGradient(
                        colors: [
                          Color(0xFFFF4242),
                          Color(0xFFFF3E67),
                          Color(0xFFFF5CA3),
                        ],
                      ),
                      onButtonTap: (ProductViewData productViewData) {
                        onPurchase(productViewData);
                      },
                    ),
                ],
              ),
            ),
            const SizedBox(
              height: 60,
            ),
            const PricingPlansFooter(),
          ],
        ),
      ),
    );
  }
}
