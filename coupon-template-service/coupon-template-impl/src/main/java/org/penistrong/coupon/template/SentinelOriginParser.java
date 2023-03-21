package org.penistrong.coupon.template;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 根据请求头部的相关字段，实现Sentinel::RequestOriginParser接口编写自定义解析器
 */
@Component
@Slf4j
public class SentinelOriginParser implements RequestOriginParser {

    // 解析调用源的name,通过获取header里的"SentinelSource"字段
    @Override
    public String parseOrigin(HttpServletRequest request) {
        log.info("Request {}, header = {}", request.getParameterMap(), request.getHeaderNames());
        return request.getHeader("SentinelSource");
    }
}
