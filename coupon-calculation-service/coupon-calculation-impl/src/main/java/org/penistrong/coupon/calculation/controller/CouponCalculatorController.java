package org.penistrong.coupon.calculation.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.calculation.service.intf.CouponCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/calculator")
public class CouponCalculatorController {
    @Autowired
    private CouponCalculationService calculationService;

    // 使用优惠券结算订单
    @PostMapping("/checkout")
    @ResponseBody
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart settlement) {
        log.info("Prepared to calculate the order: {}", JSON.toJSONString(settlement));
        return calculationService.calculateOrderPrice(settlement);
    }

    // 在优惠券列表里挨个执行试算
    // 返回包括每张优惠券其对应的优惠后价格，以及最优券的ID
    @PostMapping("/simulate")
    @ResponseBody
    public SimulationResponse simulate(@RequestBody SimulationOrder simulator) {
        log.info("Prepared to simulate the order: {}", JSON.toJSONString(simulator));
        return calculationService.simulateOrder(simulator);
    }
}
