package org.penistrong.coupon.customer;

import org.penistrong.coupon.customer.loadbalance.CanaryRuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
// 开启OpenFeign，开启后才会根据动态代理机制创建对应的远程调用服务实现，才能加入到Spring Context中再被注入到ServiceImpl里
@EnableFeignClients(basePackages = {"org.penistrong"})
public class CouponCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerApplication.class, args);
    }
}
