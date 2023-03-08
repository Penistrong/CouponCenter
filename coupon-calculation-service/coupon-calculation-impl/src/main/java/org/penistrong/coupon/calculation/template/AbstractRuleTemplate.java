package org.penistrong.coupon.calculation.template;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.calculation.api.beans.Product;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractRuleTemplate implements RuleTemplate{

    // 具体的优惠券计算子类需要实现的具体逻辑
    abstract protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota);

    @Override
    public ShoppingCart calculate(ShoppingCart order) {
        // 获取订单里所有商品的总价格
        Long orderTotalAmount = getTotalPrice(order.getProducts());

        // 以门店维度计算订单里每个门店下的商品的对应总价格
        Map<Long, Long> perShopAmount = this.getTotalPriceGroupByShop(order.getProducts());

        // 下面的门店优惠券计算规则按照每个门店只能使用单一优惠券的基础规则计算
        // 选择的优惠券信息为 ShoppingCart::couponId
        // 如果没有勾选指定优惠券，或者勾选的优惠券信息在订单信息包含的优惠券列表中找不到对应的优惠券
        // 则使用优惠券列表里的第一张优惠券
        CouponTemplateInfo template = order.getCouponInfos().stream()
                .filter(coupon -> Objects.equals(order.getCouponId(), coupon.getId()))
                .findFirst()
                .orElse(order.getCouponInfos().get(0))
                .getTemplate();

        // 获取优惠券的最低消费限制
        Long threshold = template.getRule().getDiscount().getThreshold();
        // 获取Discount里封装的quota信息(对应优惠券的满减额度或者打折比例等)
        Long quota = template.getRule().getDiscount().getQuota();
        // 获取当前优惠券对应的门店ID, 若为空则是全场通用券
        Long shopId = template.getShopId();

        // 如果当前优惠券没有shopId说明是全场券，直接覆盖在整个订单上
        Long shopTotalAmount = (shopId == null) ? orderTotalAmount : perShopAmount.get(shopId);

        // 不符合优惠券最低使用标准则无法使用
        if (shopTotalAmount == null || shopTotalAmount < threshold) {
            log.info("Totals of amount not meet the coupon's minimum threshold: RMB {} Yuan",
                    new DecimalFormat("#.00").format(threshold.doubleValue() / 100));
            order.setCost(orderTotalAmount);
            order.setCouponInfos(Collections.emptyList());
            return order;
        }

        // 在具体的优惠券子类中按其quota计算新的价格
        Long newCost = calculateNewPrice(orderTotalAmount, shopTotalAmount, quota);
        order.setCost( (newCost < minCost()) ? minCost() : newCost );
        log.info("Calculation Complete. Original Price={}, New Price={}", orderTotalAmount, order.getCost());

        return order;
    }

    // 计算订单里的商品总价
    protected Long getTotalPrice(List<Product> products) {
        return products.stream()
                .mapToLong(product -> product.getPrice() * product.getCount())
                .sum();
    }

    // 在门店维度上计算订单中每个门店下的商品价格
    protected Map<Long, Long> getTotalPriceGroupByShop(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getShopId,
                        Collectors.summingLong(p -> p.getPrice() * p.getCount())));
    }

    // 订单最低支付价格为1分钱即 1L
    protected Long minCost() {
        return 1L;
    }

    protected Long convertToDecimal(Double value) {
        return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).longValue();
    }
}
