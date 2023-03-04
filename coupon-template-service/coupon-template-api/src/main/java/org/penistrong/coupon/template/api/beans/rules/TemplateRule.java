package org.penistrong.coupon.template.api.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {

    // 折扣
    private Discount discount;

    // 每位用户的领券上限
    private Integer limitations;

    // 优惠券过期时间
    private  Long deadline;
}
