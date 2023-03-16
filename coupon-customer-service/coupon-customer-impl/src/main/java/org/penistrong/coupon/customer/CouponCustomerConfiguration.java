package org.penistrong.coupon.customer;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CouponCustomerConfiguration {
    /* 创建WebClient Bean以发起远程服务调用

     * 1.在@Configuration注解的配置类里定义的@Bean注解方法会被AnnotationConfigApplicationContext
     * 或者AnnotationConfigWebApplicationContext扫描并在上下文中进行构建
     *
     * 2.@LoadBalanced注解为WebClient.Builder构造器注入特殊的filter以实现负载均衡
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder register() {
        return WebClient.builder();
    }
}
