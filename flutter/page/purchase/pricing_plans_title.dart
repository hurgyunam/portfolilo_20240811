import 'package:cosign_app/common/v2_design/index.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class PricingPlansTitle extends StatelessWidget {
  const PricingPlansTitle({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const SizedBox(height: 28),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            SvgPicture.asset(
              "assets/img/img_cosign_logo.svg",
              width: 93.9,
              height: 25,
            ),
            const SizedBox(
              width: 4,
            ),
            Text(
              '이용권',
              style: V2Design.textStyle.head.h1,
            )
          ],
        ),
        const SizedBox(
          height: 11,
        ),
        Text(
          '지금 구매 후 AI SIGN을 무제한으로 이용하세요! ',
          style: V2Design.textStyle.body.body2,
        ),
      ],
    );
  }
}
