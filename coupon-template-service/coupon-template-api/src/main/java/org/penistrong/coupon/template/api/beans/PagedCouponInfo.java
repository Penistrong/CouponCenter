package org.penistrong.coupon.template.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedCouponInfo {
    public List<CouponInfo> couponInfos;

    public int page;

    public long total;
}
