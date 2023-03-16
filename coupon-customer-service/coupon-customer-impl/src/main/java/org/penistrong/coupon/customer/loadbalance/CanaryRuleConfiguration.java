package org.penistrong.coupon.customer.loadbalance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 名为Configuration, 但不需要Spring扫描并添加这个配置类，因为这个不是全局配置
 * 在想要采用这个配置的服务启动类上添加@LoadBalancerClient即可关联目标服务
 * 比如 @LoadBalancerClient(value = "coupon-template-service", configuration = CanaryRuleConfiguration.class)
 */
public class CanaryRuleConfiguration {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        // 在Spring上下文中声明该CanaryRule规则
        return new CanaryRule(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
