package org.penistrong.coupon.template.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CouponType {
    UNKNOWN("unknown", "0"),
    MONEY_OFF("满减券", "1"),
    DISCOUNT("打折券", "2"),
    RANDOM_DISCOUNT("随机减免券", "3"),
    LONELY_NIGHT_MONEY_OFF("晚间满减券", "4"),
    ANTI_PUA("反PUA双倍券", "5");

    private final String description;

    // 存在数据库里的code形式
    private final String code;

    // convert方法根据优惠券在数据库里的编码返回其对应的枚举对象, 缺省类型为"UNKNOWN"
    public static CouponType convert(String code) {
        return Stream.of(values())
                .filter(couponType -> couponType.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

