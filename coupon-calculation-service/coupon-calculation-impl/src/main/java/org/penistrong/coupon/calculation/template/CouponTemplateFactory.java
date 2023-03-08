package org.penistrong.coupon.calculation.template;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.template.impl.*;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.enums.CouponType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * 优惠券工厂方法，根据订单中的优惠券信息返回对应的Template
 */
@Slf4j
@Component
public class CouponTemplateFactory {

    @Autowired
    private MoneyOffTemplate moneyOffTemplate;

    @Autowired
    private DiscountTemplate discountTemplate;

    @Autowired
    private RandomDiscountTemplate randomDiscountTemplate;

    @Autowired
    private LonelyNightMoneyOffTemplate lonelyNightMoneyOffTemplate;

    @Autowired
    private DummyTemplate dummyTemplate;

    public RuleTemplate getTemplate(ShoppingCart order) {
        // 没有优惠券时，使用dummy模板计算
        if(CollectionUtils.isEmpty(order.getCouponInfos()))
            return dummyTemplate;

        // 使用订单信息里用户勾选的优惠券，如果没有勾选(null)则使用订单信息携带的优惠券列表里的第一张优惠券
        CouponTemplateInfo template = order.getCouponInfos().stream()
                .filter(coupon -> Objects.equals(order.getCouponId(), coupon.getId()))
                .findFirst()
                .orElse(order.getCouponInfos().get(0))
                .getTemplate();

        CouponType category = CouponType.convert(template.getType());

        // JDK14之后才能使用增强的Switch(JEP 361特性)，这里还是使用老Switch
        switch (category) {
            case MONEY_OFF:
                return moneyOffTemplate;
            case DISCOUNT:
                return discountTemplate;
            case RANDOM_DISCOUNT:
                return randomDiscountTemplate;
            case LONELY_NIGHT_MONEY_OFF:
                return lonelyNightMoneyOffTemplate;
            default:
                return dummyTemplate;
        }
    }
}
