package org.penistrong.coupon.calculation.template;

import org.penistrong.coupon.calculation.api.beans.ShoppingCart;

/**
 * 计算服务的顶层接口，定义calculate方法
 * 由抽象模板类AbstractRuleTemplate去实现通用的模板计算逻辑
 */
public interface RuleTemplate {

    // 通用优惠券计算方法骨架
    ShoppingCart calculate(ShoppingCart settlement);
}
