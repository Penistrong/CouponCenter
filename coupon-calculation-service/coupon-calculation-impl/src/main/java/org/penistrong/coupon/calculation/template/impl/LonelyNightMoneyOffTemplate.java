package org.penistrong.coupon.calculation.template.impl;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.template.AbstractRuleTemplate;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Slf4j
@Component
public class LonelyNightMoneyOffTemplate extends AbstractRuleTemplate implements RuleTemplate {

    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        // 午夜下单优惠翻倍 (22:00 PM ~ 次日 04:00 AM)
        // 夜间将满减额度翻倍
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 22 || hourOfDay < 4)
            quota *= 2;

        Long benefitAmount = shopTotalAmount < quota ? shopTotalAmount : quota;
        return orderTotalAmount - benefitAmount;
    }
}
