import 'package:cosign_app/common/v2_design/index.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class PricingPlansRadio extends StatelessWidget {
  final bool isActive;
  final Color activeColor;

  const PricingPlansRadio({
    super.key,
    required this.isActive,
    required this.activeColor,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        SvgPicture.asset(
          isActive ? "assets/img/radio_on.svg" : "assets/img/radio_off.svg",
          colorFilter: ColorFilter.mode(
            isActive ? activeColor : V2Design.color.border.gray01,
            BlendMode.srcIn,
          ),
        ),
      ],
    );
  }
}
