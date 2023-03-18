package org.penistrong.coupon.customer.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.customer.feign.TemplateService;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
public class TemplateServiceFallback implements TemplateService {
    @Override
    public CouponTemplateInfo getTemplate(Long id) {
        log.info("Test OpenFeign Fallback::getTemplate");
        return null;
    }

    @Override
    public Map<Long, CouponTemplateInfo> getBatchTemplate(Collection<Long> ids) {
        log.info("Test OpenFeign Fallback::getBatchTemplate");
        return null;
    }
}
