package org.penistrong.coupon.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Configuration
public class GatewayConfiguration {

    @Autowired
    private KeyResolver hostAddrKeyResolver;

    @Autowired
    @Qualifier("templateServiceRateLimiter")
    private RateLimiter templateServiceRateLimiter;

    @Autowired
    @Qualifier("customerServiceRateLimiter")
    private RateLimiter customerServiceRateLimiter;

    @Bean
    public RouteLocator declare(RouteLocatorBuilder builder){
        return builder.routes()
                .route(route -> route
                        .path("/gateway/coupon-customer/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> {
                                    c.setKeyResolver(hostAddrKeyResolver);
                                    c.setRateLimiter(customerServiceRateLimiter);
                                    c.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                }))
                        .uri("lb://coupon-customer-service")
                ).route(route -> route
                        // 如果一个请求命中多个路由，按照给定order顺序，越小优先级越高
                        .order(1)
                        .path("/gateway/template/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(t -> {
                                    t.setKeyResolver(hostAddrKeyResolver);
                                    t.setRateLimiter(templateServiceRateLimiter);
                                    t.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                }))
                        .uri("lb://coupon-template-service")
                ).route(route -> route
                        .path("/gateway/calculator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coupon-calculation-service")
                ).build();
    }
}
