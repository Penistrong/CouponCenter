package org.penistrong.coupon.template.dao.converter;

import org.penistrong.coupon.template.api.enums.CouponType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CouponTypeConverter implements AttributeConverter<CouponType, String> {

    @Override
    public String convertToDatabaseColumn(CouponType couponType) {
        return couponType.getCode();
    }

    @Override
    public CouponType convertToEntityAttribute(String code) {
        return CouponType.convert(code);
    }
}
