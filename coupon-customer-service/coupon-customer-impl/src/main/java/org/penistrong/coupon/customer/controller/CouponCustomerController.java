package org.penistrong.coupon.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.customer.api.beans.CouponSearchParams;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.customer.service.intf.CouponCustomerService;
import org.penistrong.coupon.template.api.beans.PagedCouponInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/coupon-customer")
public class CouponCustomerController {

    @Autowired
    private CouponCustomerService customerService;

    // 领取优惠券
    @PostMapping("/requestCoupon")
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        return customerService.requestCoupon(request);
    }

    // 删除优惠券(置为失效状态)
    @DeleteMapping("/deleteCoupon")
    public void deleteCoupon(@RequestParam("userId") Long userId, @RequestParam("couponId") Long couponId) {
        customerService.deleteCoupon(userId, couponId);
    }

    // 订单优惠券价格试算
    @PostMapping("/simulateOrder")
    public SimulationResponse simulate(@Valid @RequestBody SimulationOrder order) {
        return customerService.simulateOrderPrice(order);
    }

    // 订单结算
    @PostMapping("/placeOrder")
    public ShoppingCart checkout(@Valid @RequestBody ShoppingCart cart) {
        return customerService.placeOrder(cart);
    }

    @PostMapping("/searchCoupon")
    // 搜索优惠券，已封装搜索参数，并返回分页查询结果
    public PagedCouponInfo searchCoupon(@Valid @RequestBody CouponSearchParams request) {
        return customerService.searchCoupons(request);
    }
}
