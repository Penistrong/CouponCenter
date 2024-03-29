package org.penistrong.coupon.customer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.customer.api.beans.CouponSearchParams;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.customer.event.CouponProducer;
import org.penistrong.coupon.customer.service.intf.CouponCustomerService;
import org.penistrong.coupon.template.api.beans.PagedCouponInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;


@Slf4j
// 添加RefreshScope注解，将来自Nacos Config的属性变动动态同步到Controller中
// 或者使用Spring Environment也可读取当前Spring上下文中动态变动的环境配置
@RefreshScope
@RestController
@RequestMapping("/coupon-customer")
public class CouponCustomerController {

    // 将Nacos配置中心提供的coupon-customer-service.yml中的属性注入
    // 实现动态配置推送和业务开关，注意默认值为false(即使Nacos Config连接异常),配合@RefreshScope使用
    // 官网不推荐用@NacosValue(value = "${disableCouponRequest:false}", autoRefreshed = true)
    @Value("${disableCouponRequest:false}")
    private Boolean disableCoupon;

    @Autowired
    private CouponCustomerService customerService;

    @Autowired
    private CouponProducer couponProducer;

    // 消息队列发布生产消息
    @PostMapping("requestCouponEvent")
    public void requestCouponEvent (@Valid @RequestBody RequestCoupon coupon) {
        couponProducer.produceRequestCouponEvent(coupon);
    }

    @PostMapping("requestCouponDelayEvent")
    public void requestCouponDelayedEvent (@Valid @RequestBody RequestCoupon coupon) {
        couponProducer.produceRequestCouponDelayEvent(coupon);
    }

    @DeleteMapping("deleteCouponEvent")
    public void deleteCouponEvent (@RequestParam("userId") Long userId,
                                   @RequestParam("couponId") Long couponId) {
        couponProducer.produceDeleteCouponEvent(userId, couponId);
    }

    // 领取优惠券
    @PostMapping("/requestCoupon")
    @SentinelResource(value = "customer::requestCoupon")
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

    // 删除优惠券模板及所有由该模板派生的优惠券(全部置为失效状态)
    // 在当前用户服务模块的Controller::handler这里，为了让全局回滚操作生效，在递归调用中需要将产生的异常上抛至该处以被捕获
    // 小心AOP的统一异常拦截器
    @DeleteMapping("/deleteCouponTemplate")
    @GlobalTransactional(name = "coupon-customer-service", rollbackFor = Exception.class)
    public void deleteCouponTemplate(@RequestParam("templateId") Long templateId) {
        customerService.deleteCouponTemplate(templateId);
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
    @SentinelResource(value = "customer::searchCoupon")
    // 搜索优惠券，已封装搜索参数，并返回分页查询结果
    public PagedCouponInfo searchCoupon(@Valid @RequestBody CouponSearchParams request) {
        return customerService.searchCoupons(request);
    }
}
