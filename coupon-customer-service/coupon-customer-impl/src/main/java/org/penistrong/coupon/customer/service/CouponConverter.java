package org.penistrong.coupon.customer.service;

import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.template.api.beans.CouponInfo;

public class CouponConverter {
    public static CouponInfo convertToCouponInfo(Coupon coupon) {
        return CouponInfo.builder()
                .id(coupon.getId())
                .templateId(coupon.getTemplateId())
                .userId(coupon.getUserId())
                .status(coupon.getStatus().getCode())
                .shopId(coupon.getShopId())
                .template(coupon.getTemplateInfo())
                .build();
    }
}
