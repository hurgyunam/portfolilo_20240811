import 'package:cosign_app/common/const/colors.dart';
import 'package:cosign_app/common/const/config.dart';
import 'package:cosign_app/common/const/strings.dart';
import 'package:cosign_app/common/v2_design/index.dart';
import 'package:cosign_app/datas/model/auth/purchase_footer_text.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans_footer_item.dart';
import 'package:flutter/material.dart';

class PricingPlansFooter extends StatelessWidget {
  const PricingPlansFooter({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width,
      decoration: const BoxDecoration(color: r245g245b245a100),
      padding: const EdgeInsets.only(
        left: 16,
        top: 16,
        right: 16,
        bottom: 40,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '이용안내',
            style: TextStyle(
              fontSize: 12,
              fontFamily: notoSansKR,
              color: V2Design.color.font.dark02,
            ),
          ),
          SizedBox(
            height: 8,
          ),
          PricingPlansFooterItem(
              [PurchaseFooterText(text: '이용권은 부가세(VAT) 포함 가격입니다.')]),
          SizedBox(
            height: 4,
          ),
          PricingPlansFooterItem(
              [PurchaseFooterText(text: '이용권 가격과 기능/혜택은 변경될 수 있습니다.')]),
          SizedBox(
            height: 4,
          ),
          PricingPlansFooterItem([
            PurchaseFooterText(
                text:
                    '모든 이용권은 결제와 동시에 서비스가 개시되므로 환불이 원칙적으로 불가능하며, 상세한 환불 규정은 각(Google, Apple) 앱스토어의 결제 정책에 따릅니다.')
          ]),
          SizedBox(
            height: 4,
          ),
          PricingPlansFooterItem([
            PurchaseFooterText(
                text:
                    '정기결제는 1개월을 주기로 최초 구매한 날과 동일한 날짜에 자동 결제되며, 해당월에 동일한 날짜가 없을 경우 말일에 결제됩니다.')
          ]),
          SizedBox(
            height: 4,
          ),
          PricingPlansFooterItem([
            PurchaseFooterText(
                text:
                    '정기결제는 이용기간 중 언제든지 각 앱스토어를 통해 자동 결제를 해지(취소)할 수 있습니다. 해지를 할 경우 결제월의 이용기간까지는 정상적으로 서비스 이용이 가능합니다.')
          ]),
          SizedBox(
            height: 4,
          ),
          PricingPlansFooterItem(
            [
              PurchaseFooterText(
                text: '이용권 구매 시 ',
              ),
              PurchaseFooterText(
                text: '개인정보취급방침',
                url: "$webViewRoot/policy/privacy",
              ),
              PurchaseFooterText(
                text: ' 및 ',
              ),
              PurchaseFooterText(
                text: '이용 약관',
                url: "$webViewRoot/policy/service",
              ),
              PurchaseFooterText(
                text: '에 동의한 것으로 간주됩니다.',
              ),
            ],
          ),
        ],
      ),
    );
  }
}
