package org.penistrong.coupon.customer;

import org.penistrong.coupon.customer.loadbalance.CanaryRuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 三合一启动，改造成微服务架构时要更改，变成完全独立的3个服务
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@ComponentScan(basePackages = {"org.penistrong"})
// 扫描DAO的JPARepository
@EnableJpaRepositories(basePackages = {"org.penistrong"})
//扫描JPA使用的实体类
@EntityScan(basePackages = {"org.penistrong"})
@EnableDiscoveryClient
@LoadBalancerClient(value = "coupon-template-service", configuration = CanaryRuleConfiguration.class)
public class CouponCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerApplication.class, args);
    }
}
