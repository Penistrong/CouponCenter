package org.penistrong.coupon.customer.feign;

import org.penistrong.coupon.customer.feign.fallback.TemplateServiceFallback;
import org.penistrong.coupon.customer.feign.fallback.TemplateServiceFallbackFactory;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

/**
 * 添加@FeignClient注解，标记该接口被OpenFeign托管
 * 可选属性path类似RequestMapping中的path, 即统一的前置访问路径
 * fallback指定降级类，即远程服务调用失败时会自己自动使用降级类里的同名方法
 */
@FeignClient(value = "coupon-template-service", path = "/template",
             fallback = TemplateServiceFallback.class,
             fallbackFactory = TemplateServiceFallbackFactory.class)
public interface TemplateService {

    // 读取优惠券
    @GetMapping("/getTemplate")
    CouponTemplateInfo getTemplate(@RequestParam("id") Long id);

    // 批量读取优惠券
    @GetMapping("/getBatchTemplates")
    Map<Long, CouponTemplateInfo> getBatchTemplate(@RequestParam("ids") Collection<Long> ids);

    @DeleteMapping("/deleteTemplate")
    void deleteTemplate(@RequestParam("id") Long id);
}
