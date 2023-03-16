package org.penistrong.coupon.customer.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCoupon {

    // 用户领券时发送userID
    @NotNull
    private Long userId;

    // 优惠券对应的模板ID
    @NotNull
    private Long couponTemplateId;

    // LoadBalancer - 测试流量打标字段，将trafficVersion添加到WebClient的Header中
    private String trafficVersion;
}
