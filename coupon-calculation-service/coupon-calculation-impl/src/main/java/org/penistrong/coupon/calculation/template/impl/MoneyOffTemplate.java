package org.penistrong.coupon.calculation.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.template.AbstractRuleTemplate;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoneyOffTemplate extends AbstractRuleTemplate implements RuleTemplate {

    @Override
    protected Long calculateNewPrice(Long totalAmount, Long shopAmount, Long quota) {
        // benefitAmount是满减券减免的额度
        // 满减券的quota(Long)就是减免的额度
        Long benefitAmount = shopAmount < quota ? shopAmount : quota;
        return totalAmount - benefitAmount;
    }
}
