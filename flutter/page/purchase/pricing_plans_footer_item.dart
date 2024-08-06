import 'package:cosign_app/common/v2_design/index.dart';
import 'package:cosign_app/common/v2_design/text_style.dart';
import 'package:cosign_app/datas/model/auth/purchase_footer_text.dart';
import 'package:cosign_app/elements/content/cosign_inapp_web_view.dart';
import 'package:cosign_app/page/common/popup_page.dart';
import 'package:flutter/material.dart';

class PricingPlansFooterItem extends StatelessWidget {
  final List<PurchaseFooterText> messages;
  const PricingPlansFooterItem(this.messages, {super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          margin: const EdgeInsets.only(
            top: 5,
          ),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(
              100,
            ),
            color: V2Design.color.font.dark02,
          ),
          width: 4,
          height: 4,
        ),
        const SizedBox(
          width: 4,
        ),
        Container(
          width: MediaQuery.of(context).size.width - 40,
          child: Wrap(
            children: messages.map((message) {
              return GestureDetector(
                onTap: () {
                  final url = message.url;
                  if (url != null) {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => PopupPage(
                          needLog: true,
                          title: message.text.replaceAll(" ", ""),
                          child: CosignInappWebView(url: url),
                        ),
                      ),
                    );
                  }
                },
                child: Text(
                  message.text,
                  style: TextStyle(
                    fontFamily: fontNotoSansKR,
                    color: V2Design.color.font.dark02,
                    fontSize: 10,
                    fontWeight:
                        message.url != null ? FontWeight.w700 : FontWeight.w400,
                    decoration: message.url != null
                        ? TextDecoration.underline
                        : TextDecoration.none,
                  ),
                ),
              );
            }).toList(),
          ),
        ),
      ],
    );
  }
}
