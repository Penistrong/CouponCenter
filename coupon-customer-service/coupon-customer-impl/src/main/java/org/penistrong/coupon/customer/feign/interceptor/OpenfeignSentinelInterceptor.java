package org.penistrong.coupon.customer.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenfeignSentinelInterceptor implements RequestInterceptor {

    // 拦截OpenFeign发起的请求，在请求的header中添加SentinelSource属性，让下游服务知道来源
    @Override
    public void apply(RequestTemplate template) {
        template.header("SentinelSource", "coupon-customer-service");
    }
}
