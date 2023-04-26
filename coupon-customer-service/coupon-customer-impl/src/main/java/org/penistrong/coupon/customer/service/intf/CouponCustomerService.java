package org.penistrong.coupon.customer.service.intf;

import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.api.beans.CouponSearchParams;
import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.template.api.beans.PagedCouponInfo;

/**
 * 用户服务接口
 */
public interface CouponCustomerService {
    // 领券
    Coupon requestCoupon(RequestCoupon request);

    // 核销优惠券
    ShoppingCart placeOrder(ShoppingCart cart);

    // 优惠价格试算
    SimulationResponse simulateOrderPrice(SimulationOrder order);

    // 删除优惠券(设为失效)
    void deleteCoupon(Long userId, Long couponId);

    // 删除优惠券模板及所有由该模板派生的优惠券(全部置为失效状态)
    void deleteCouponTemplate(Long templateId);

    // 拉取用户拥有的优惠券列表
    PagedCouponInfo searchCoupons(CouponSearchParams request);
}
