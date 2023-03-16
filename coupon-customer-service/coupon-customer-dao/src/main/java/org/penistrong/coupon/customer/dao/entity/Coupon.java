package org.penistrong.coupon.customer.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.penistrong.coupon.customer.api.enums.CouponStatus;
import org.penistrong.coupon.customer.dao.converter.CouponStatusConverter;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // 对应优惠券模板ID, 不使用多表级联查询或者外键等一一映射方案，尽可能单表查询
    // 微服务架构用数据冗余或数据异构方案应对高并发场景下的吞吐量和性能，底层的DB反而是最影响性能的一环
    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 使用数据冗余方式，冗余一个coupon_template表中已有的shop_id字段
    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    // 表中没有的字段使用@Transient注解表示其不属于持久化范畴
    @Transient
    private CouponTemplateInfo templateInfo;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private Date createdTime;
}

