package org.penistrong.coupon.customer.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 封装的查询参数对象，用于用户分页查询优惠券
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponSearchParams {

    @NotNull
    private Long userId;
    private Long shopId;
    private Integer couponStatusCode;

    private int page;

    private int pageSize;
}
