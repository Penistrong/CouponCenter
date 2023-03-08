package org.penistrong.coupon.calculation.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.template.AbstractRuleTemplate;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class RandomDiscountTemplate extends AbstractRuleTemplate implements RuleTemplate {

    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        // 随机折扣券的quota表示的是随机立减额度的上限(当然不能超过总值
        long maxBenefit = Math.min(shopTotalAmount, quota);
        int reductionAmount = new Random().nextInt((int) maxBenefit);
        Long newCost = orderTotalAmount - reductionAmount;

        log.info("Random Discount at original price={} with random reduction={}, new price={}",
                orderTotalAmount, reductionAmount, newCost);
        return newCost;
    }
}
