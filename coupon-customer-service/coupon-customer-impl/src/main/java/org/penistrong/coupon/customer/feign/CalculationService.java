package org.penistrong.coupon.customer.feign;

import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "coupon-calculation-service", path = "/calculator")
public interface CalculationService {

    // 订单结算
    @PostMapping("/checkout")
    ShoppingCart checkout(ShoppingCart cart);

    // 订单试算
    @PostMapping("/simulate")
    SimulationResponse simulate(SimulationOrder order);
}
