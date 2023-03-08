package org.penistrong.coupon.calculation.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // 商品价格
    private long price;

    // 此商品在购物车中的数量
    // 真实情况的零售业，计件单位不应该是整数，简化一下
    private Integer count;

    // 商品销售门店ID
    private Long shopId;
}
