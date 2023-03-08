package org.penistrong.coupon.calculation.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.calculation.service.intf.CouponCalculationService;
import org.penistrong.coupon.calculation.template.CouponTemplateFactory;
import org.penistrong.coupon.calculation.template.RuleTemplate;
import org.penistrong.coupon.template.api.beans.CouponInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 优惠券计算服务业务层实现
 * 对于上层业务要尽可能地屏蔽其对底层业务复杂度的感知(开闭原则)
 */
@Slf4j
@Service
public class CouponCalculationServiceImpl implements CouponCalculationService {

    @Autowired
    private CouponTemplateFactory couponTemplateFactory;

    // 使用优惠券结算购物车订单
    @Override
    public ShoppingCart calculateOrderPrice(@RequestBody ShoppingCart cart) {
        log.info("Calculating order price with shopping-cart: {}", JSON.toJSONString(cart));
        RuleTemplate ruleTemplate = couponTemplateFactory.getTemplate(cart);
        return ruleTemplate.calculate(cart);
    }

    // 对拥有的所有优惠券，每个都进行一次试算，找出bestCoupon
    @Override
    public SimulationResponse simulateOrder(@RequestBody SimulationOrder order) {
        SimulationResponse response = new SimulationResponse();
        long minOrderPrice = Long.MAX_VALUE;

        // 对每一个优惠券计算一次订单价格
        for (CouponInfo coupon: order.getCouponInfos()) {
            ShoppingCart cart = new ShoppingCart();
            cart.setProducts(order.getProducts());
            cart.setCouponInfos(Lists.newArrayList(coupon));    // 列表里只放了1张优惠券
            cart = couponTemplateFactory.getTemplate(cart).calculate(cart);

            Long couponId = coupon.getId();
            Long orderPrice = cart.getCost();

            // 存入当前优惠券试算后得到的订单优惠价格
            response.getCouponToOrderPrice().put(couponId, orderPrice);

            // 提取最优coupon的ID
            if (minOrderPrice > orderPrice) {
                response.setBestCouponId(couponId);
                minOrderPrice = orderPrice;
            }
        }
        return response;
    }
}
