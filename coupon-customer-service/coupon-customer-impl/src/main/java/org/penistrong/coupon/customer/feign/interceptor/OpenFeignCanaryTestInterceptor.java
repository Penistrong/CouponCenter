package org.penistrong.coupon.customer.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.penistrong.coupon.customer.constant.Constant.TRAFFIC_VERSION;

@Configuration
public class OpenFeignCanaryTestInterceptor implements RequestInterceptor {

    // 使用OpenFeign调用时，如果要开启金丝雀测试，则此拦截器会在请求头设置对应字段
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 利用SpringWeb的RequestContextHolder获取到当前处理的请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestTemplate.header(TRAFFIC_VERSION, request.getHeader(TRAFFIC_VERSION));
        }
    }
}
