package org.penistrong.coupon.calculation.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.template.AbstractRuleTemplate;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscountTemplate extends AbstractRuleTemplate implements RuleTemplate {

    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        Long discounted_shopAmount = convertToDecimal(shopTotalAmount * (quota.doubleValue() / 100));
        log.info("Discount at original price={} with discount={}%, new price={}",
                orderTotalAmount, quota, discounted_shopAmount);
        return orderTotalAmount - shopTotalAmount + discounted_shopAmount;
    }
}
