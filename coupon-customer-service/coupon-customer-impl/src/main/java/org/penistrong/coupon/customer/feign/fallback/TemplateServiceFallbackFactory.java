package org.penistrong.coupon.customer.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.customer.feign.TemplateService;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * 利用Fallback工厂类指定降级逻辑
 */
@Slf4j
@Component
public class TemplateServiceFallbackFactory implements FallbackFactory {
    @Override
    public TemplateService create(Throwable cause) {
        return new TemplateService() {
            @Override
            public CouponTemplateInfo getTemplate(Long id) {
                log.info("Test Fallback Factory with Throwable cause");
                return null;
            }

            @Override
            public Map<Long, CouponTemplateInfo> getBatchTemplate(Collection<Long> ids) {
                log.info("Test Fallback Factory with Throwable cause");
                return null;
            }
        };
    }
}
