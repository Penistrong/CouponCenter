package org.penistrong.coupon.template.api.beans;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.penistrong.coupon.template.api.beans.rules.TemplateRule;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTemplateInfo {

    private Long id;

    // 优惠券名称
    @NotNull
    private String name;

    // 优惠券描述
    @NotNull
    private String desc;

    // 优惠券类型
    @NotNull
    private String type;

    // 适用门店id，若为空则为全场通用券
    private Long shopId;

    // 优惠券规则
    @NotNull
    private TemplateRule rule;

    private boolean available;
}
