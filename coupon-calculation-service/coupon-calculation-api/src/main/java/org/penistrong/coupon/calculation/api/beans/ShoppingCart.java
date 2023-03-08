package org.penistrong.coupon.calculation.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.penistrong.coupon.template.api.beans.CouponInfo;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    // 订单里的商品列表
    @NotEmpty
    private List<Product> products;

    // 结算时使用的优惠券
    private Long couponId;

    // 到目前为止的学习阶段，虽然计算服务只涉及单张优惠券，为了多张的扩展性定义为List
    private List<CouponInfo> couponInfos;

    // 订单最终价格
    private long cost;

    // 用户ID
    private Long userId;
}
