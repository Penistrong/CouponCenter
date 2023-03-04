package org.penistrong.coupon.template.dao;

import org.penistrong.coupon.template.dao.entity.CouponTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 优惠券模板的数据访问对象DAO(Data Access Object)
 * 使用JPA的约定大于配置的思想，将待查询字段定义在接口的方法名中，发起调用时JPA会自动将其转换为可执行的SQL语句
 * 构造接口的方法名需要遵循 {find/count}[可选范围]By{查询字段}{连接词}[...] 的大致结构
 * 查询字段要与对应的Entity类中定义的字段名称保持一致
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Long> {

    // 根据Shop ID查询出某店下的所有券模板
    List<CouponTemplate> findAllByShopId(Long shopId);

    // 单表表内查询 + 分页支持
    Page<CouponTemplate> findAllByIdIn(List<Long> Id, Pageable page);

    // 根据Shop ID + 可用状态查询某店下所有可用的券模板的总数
    Integer countByShopIdAndAvailable(Long shopId, Boolean available);

    // 对于复杂查询，使用接口名会导致名称过长且难以维护
    // 解决方法: 1.自定义SQL 2.构造Example对象进行查找
    @Modifying
    @Query("UPDATE CouponTemplate c SET c.available = 0 WHERE c.id = :id")
    int makeCouponUnavailable(@Param("id") Long id);

}
