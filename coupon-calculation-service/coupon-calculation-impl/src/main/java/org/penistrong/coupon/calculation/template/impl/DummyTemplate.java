package org.penistrong.coupon.calculation.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.template.AbstractRuleTemplate;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.springframework.stereotype.Component;

/**
 * 空实现，相当于没有券
 */
@Slf4j
@Component
public class DummyTemplate extends AbstractRuleTemplate implements RuleTemplate {

    @Override
    public ShoppingCart calculate(ShoppingCart order) {
        Long orderTotalAmount = getTotalPrice((order.getProducts()));
        order.setCost(orderTotalAmount);

        return order;
    }

    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        return orderTotalAmount;
    }
}
