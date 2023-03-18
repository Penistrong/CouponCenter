package org.penistrong.coupon.customer.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
// 添加RefreshScope注解，将来自Nacos Config的属性变动动态同步到Controller中
// 或者使用Spring Environment也可读取当前Spring上下文中动态变动的环境配置
@RefreshScope
@RestController
@RequestMapping("/coupon-customer")
public class CouponCustomerController {

    @Autowired
    private CouponCustomerService customerService;

    // 将Nacos配置中心提供的coupon-customer-service.yml中的属性注入
    // 实现动态配置推送和业务开关，注意默认值为false(即使Nacos Config连接异常),配合@RefreshScope使用
    // 官网不推荐用@NacosValue(value = "${disableCouponRequest:false}", autoRefreshed = true)
    @Value("${disableCouponRequest:false}")
    private Boolean disableCoupon;

    // 领取优惠券
    @PostMapping("/requestCoupon")
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        if (disableCoupon) {
            log.info("Nacos Config told us haul some time to Request Coupon");
            return null;
        }
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
