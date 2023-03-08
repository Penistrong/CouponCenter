package org.penistrong.coupon.customer.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponStatus {

    AVAILABLE("未使用", 1),
    USED("已使用", 2),
    INACTIVE("已失效", 3);

    private final String desc;
    private final Integer code;

    public static CouponStatus convert(Integer code) {
        if (code == null) return null;
        return Stream.of(values())
                .filter(couponStatus -> couponStatus.code.equals(code))
                .findAny()
                .orElse(null);
    }
}
