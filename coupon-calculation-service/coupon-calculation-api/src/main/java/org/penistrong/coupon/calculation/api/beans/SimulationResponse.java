package org.penistrong.coupon.calculation.api.beans;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class SimulationResponse {

    // 订单中最省钱的coupon
    private Long bestCouponId;

    // 每个coupon对应的订单价格
    private Map<Long, Long> couponToOrderPrice = Maps.newHashMap();
}
