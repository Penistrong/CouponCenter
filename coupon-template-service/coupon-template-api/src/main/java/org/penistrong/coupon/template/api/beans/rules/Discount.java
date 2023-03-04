package org.penistrong.coupon.template.api.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    /**
     * 1.满减券: 减免额度
     * 2.折扣券: * = *% (e.g. 90 = 90%)
     * 3.随机立减券: 随机立减额度上限
     * 4.晚间特别优惠券: quota表示日间优惠额度，晚间时段优惠翻倍
     **/
    private Long quota;

    // 券的消费门槛(达到最低消费门槛才可使用优惠券)，单位为分
    // 直接使用以分为单位的Long类型，避免使用Double类型到处转换BigDecimal
    private Long threshold;
}
