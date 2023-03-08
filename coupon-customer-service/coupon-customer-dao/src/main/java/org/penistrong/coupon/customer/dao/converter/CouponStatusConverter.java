package org.penistrong.coupon.customer.dao.converter;

import org.penistrong.coupon.customer.api.enums.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {
    // 将DB中表coupon里的status(int)字段转换为Enums
    // 继承AttributeConverter<T, E>

    // Enums转换为DB int value
    @Override
    public Integer convertToDatabaseColumn(CouponStatus couponStatus) {
        return couponStatus.getCode();
    }

    // DB int value转换为Enums
    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.convert(code);
    }
}
