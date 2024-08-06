import 'package:cosign_app/common/const/productId.dart';
import 'package:cosign_app/common/extension/int_ext.dart';
import 'package:cosign_app/common/v2_design/index.dart';
import 'package:cosign_app/common/v2_design/text_style.dart';
import 'package:cosign_app/elements/auth/purchase/pricing_plans_radio.dart';
import 'package:flutter/material.dart';

class PricingPlansItem extends StatefulWidget {
  final ProductViewData subscription, day30;
  final String descriptionEmoji;
  final String description;
  final String buttonText;
  final LinearGradient gradient;
  final Color color;
  final void Function(ProductViewData product) onButtonTap;

  const PricingPlansItem({
    super.key,
    required this.subscription,
    required this.day30,
    required this.color,
    required this.gradient,
    required this.description,
    required this.descriptionEmoji,
    required this.buttonText,
    required this.onButtonTap,
    // required this.ticket,
  });

  @override
  State<PricingPlansItem> createState() => _PricingPlansItemState();
}

class _PricingPlansItemState extends State<PricingPlansItem> {
  bool isSelectedSubscription = true;

  @override
  Widget build(BuildContext context) {
    final day30Price = widget.day30.storePrice;
    final subscriptionPrice = widget.subscription.storePrice;

    int? discountPrice;
    if (day30Price != null && subscriptionPrice != null) {
      discountPrice = (day30Price - subscriptionPrice).round();
    }

    return Container(
      decoration: BoxDecoration(
        color: V2Design.color.background.white,
        border: Border.all(
          color: V2Design.color.border.gray03,
        ),
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: V2Design.color.background.black.withOpacity(0.05),
            offset: Offset(0, 3),
            spreadRadius: 0,
            blurRadius: 7,
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                decoration: BoxDecoration(
                  border: Border(
                    right: BorderSide(
                      color: V2Design.color.border.gray03,
                    ),
                    bottom: BorderSide(
                      color: V2Design.color.border.gray03,
                    ),
                  ),
                  borderRadius: const BorderRadius.only(
                    bottomRight: Radius.circular(8),
                  ),
                ),
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 4,
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      widget.descriptionEmoji,
                    ),
                    ShaderMask(
                      blendMode: BlendMode.srcIn,
                      shaderCallback: (Rect bounds) =>
                          widget.gradient.createShader(
                        Rect.fromLTWH(0, 0, bounds.width, bounds.height),
                      ),
                      child: Text(
                        widget.description,
                        style: V2Design.textStyle.caption.cap1m.copyWith(
                          color: widget.color,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(
            height: 8,
          ),
          Padding(
            padding: const EdgeInsets.symmetric(
              vertical: 8,
              horizontal: 16,
            ),
            child: Column(
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(
                    vertical: 12,
                  ),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          GestureDetector(
                            onTap: () {
                              setState(() {
                                isSelectedSubscription = true;
                              });
                            },
                            child: Row(
                              children: [
                                PricingPlansRadio(
                                  isActive: isSelectedSubscription,
                                  activeColor: widget.color,
                                ),
                                const SizedBox(
                                  width: 4,
                                ),
                                Text(
                                  widget.subscription.title,
                                  style: V2Design.textStyle.body.body2,
                                ),
                              ],
                            ),
                          ),
                          Container(
                            decoration: BoxDecoration(
                              gradient: widget.gradient,
                              borderRadius: BorderRadius.circular(28),
                            ),
                            padding: const EdgeInsets.symmetric(
                              vertical: 4,
                              horizontal: 8,
                            ),
                            child: Row(
                              crossAxisAlignment: CrossAxisAlignment.end,
                              children: [
                                Text(
                                  "${widget.subscription.discountPercentage}",
                                  style: V2Design.textStyle.price.p14B.copyWith(
                                    color: V2Design.color.font.white,
                                  ),
                                ),
                                Text(
                                  "%",
                                  style:
                                      V2Design.textStyle.caption.cap1m.copyWith(
                                    color: V2Design.color.font.white,
                                  ),
                                ),
                                const SizedBox(width: 1),
                                Text(
                                  "할인",
                                  style: TextStyle(
                                    fontSize: 12,
                                    fontWeight: FontWeight.w700,
                                    fontFamily: fontFamilySpoqa,
                                    decoration: TextDecoration.none,
                                    color: V2Design.color.font.white,
                                  ),
                                ),
                              ],
                            ),
                          )
                        ],
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          if (discountPrice != null) ...[
                            Text(
                              "${discountPrice.setComma()}원 절약!",
                              style: V2Design.textStyle.caption.cap2m,
                            ),
                            const SizedBox(
                              width: 4,
                            ),
                          ],
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Text(
                                "월",
                                style: V2Design.textStyle.head.h6.copyWith(
                                  color: widget.color,
                                ),
                              ),
                              const SizedBox(
                                width: 2,
                              ),
                              Text(
                                "${widget.subscription.storePrice?.round().setComma()}",
                                style: V2Design.textStyle.head.h2.copyWith(
                                  color: widget.color,
                                  height: 1.2,
                                ),
                              ),
                              const SizedBox(
                                width: 2,
                              ),
                              Text(
                                "원",
                                style: V2Design.textStyle.head.h6.copyWith(
                                  color: widget.color,
                                ),
                              )
                            ],
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                Container(
                  width: double.infinity,
                  height: 1,
                  color: V2Design.color.border.gray05,
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(
                    vertical: 12,
                  ),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          GestureDetector(
                            onTap: () {
                              setState(() {
                                isSelectedSubscription = false;
                              });
                            },
                            child: Row(
                              children: [
                                PricingPlansRadio(
                                  isActive: !isSelectedSubscription,
                                  activeColor: widget.color,
                                ),
                                const SizedBox(
                                  width: 4,
                                ),
                                Text(
                                  widget.day30.title,
                                  style: V2Design.textStyle.body.body2,
                                ),
                              ],
                            ),
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              Text(
                                "${widget.day30.storePrice?.round().setComma()}",
                                style: V2Design.textStyle.price.p16B.copyWith(),
                              ),
                              Text(
                                "원",
                                style: V2Design.textStyle.body.body4.copyWith(),
                              )
                            ],
                          ),
                        ],
                      ),
                      Row(
                        children: [
                          const SizedBox(
                            width: 30,
                          ),
                          if (isSelectedSubscription == false)
                            Text(
                              "⚠️ 할인 적용되지 않는 상품입니다.",
                              style: V2Design.textStyle.caption.cap2m.copyWith(
                                color: Color(0xFFF51230),
                              ),
                            )
                        ],
                      ),
                    ],
                  ),
                ),
                const SizedBox(
                  height: 8,
                ),
                GestureDetector(
                  onTap: () {
                    if (isSelectedSubscription) {
                      widget.onButtonTap(widget.subscription);
                    } else {
                      widget.onButtonTap(widget.day30);
                    }
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      gradient: widget.gradient,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    height: 52,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          widget.buttonText,
                          style: V2Design.textStyle.head.h3.copyWith(
                            color: V2Design.color.font.white,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(
                  height: 8,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
