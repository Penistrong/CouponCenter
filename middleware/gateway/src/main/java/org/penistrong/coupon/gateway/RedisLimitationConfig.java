package org.penistrong.coupon.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RedisLimitationConfig {

    // 限流维度: 以Remote Host Address作为限流规则统计的维度
    // 来自同一个Remote Host的请求会被限流规则统计为一组
    @Bean
    @Primary
    public KeyResolver remoteHostLimitationKey() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    // 以下三个方法都是基于令牌桶算法进行限流
    // 第一个参数表示每秒发放的令牌个数，第二个参数表示令牌桶的容量
    @Bean("templateServiceRateLimiter")
    public RedisRateLimiter templateServiceRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    @Bean("customerServiceRateLimiter")
    public RedisRateLimiter customerServiceRateLimiter() {
        return new RedisRateLimiter(20, 40);
    }

    // 默认限流器
    @Bean("defaultRateLimiter")
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(50, 100);
    }
}
