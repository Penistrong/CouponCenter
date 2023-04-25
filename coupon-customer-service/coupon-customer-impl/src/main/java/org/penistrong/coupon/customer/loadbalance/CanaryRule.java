package org.penistrong.coupon.customer.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.penistrong.coupon.customer.constant.Constant.TRAFFIC_VERSION;

/**
 * 自定义负载均衡策略类，可以单独取出作为1个公共组件提供服务
 * LoadBalancer的默认负载均衡策略只有Random和RoundRobin两种
 */
@Slf4j
public class CanaryRule implements ReactorServiceInstanceLoadBalancer {

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;

    // 使用原子整型类作为轮询策略的种子
    final AtomicInteger position;

    public CanaryRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                      String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        position = new AtomicInteger(new Random().nextInt(1000));
    }

    /**
     * LoadBalancer标准接口，负载均衡策略选择服务器的入口方法
     * @param request
     * @return
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, request));
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            List<ServiceInstance> serviceInstances,
            Request request) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, request);

        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }

        return serviceInstanceResponse;
    }

    Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        // 当注册中心无可用服务实例时抛出异常
        if (CollectionUtils.isEmpty(instances)){
            log.warn("No service instance available, serviceId = {}", serviceId);
            return new EmptyResponse();
        }

        // 从请求的Header中获取特定的流量打标值
        // 以下代码只适用于WebClient调用，如果使用RestTemplate或者Feign则需要编写额外代码
        DefaultRequestContext context = (DefaultRequestContext) request.getContext();
        RequestData requestData = (RequestData) context.getClientRequest();
        HttpHeaders headers = requestData.getHeaders();
        // 获取Header里的流量标记
        String trafficVersion = headers.getFirst(TRAFFIC_VERSION);
        log.info("Find request header with field 'traffic-version' = {}", trafficVersion);

        // 如果Header里没有打标，则使用RoundRobin规则轮询服务实例
        if (StringUtils.isBlank(trafficVersion)) {
            // 过滤掉所有参与金丝雀测试的节点，即Nacos MetaData中包含标记的节点(该标记在Nacos对服务的某个实例自己添加或者在.yml中配置)
            // 再从剩余不参与金丝雀测试的节点中执行RoundRobin
            List<ServiceInstance> noneCanaryInstances = instances.stream()
                    .filter(e -> !e.getMetadata().containsKey(TRAFFIC_VERSION))
                    .toList();
            return getRoundRobinInstance(noneCanaryInstances);
        }

        // 对于打标且标记字段符合实际定义值的金丝雀服务器，仍然使用RoundRobin挑出其中的一个实例作为负载均衡发送请求的目标服务器实例
        List<ServiceInstance> canaryInstances = instances.stream()
                .filter(e -> {
                    String trafficVersionInMetaData = e.getMetadata().get(TRAFFIC_VERSION);
                    return StringUtils.equalsIgnoreCase(trafficVersionInMetaData, trafficVersion);
                }).toList();

        return getRoundRobinInstance(canaryInstances);
    }

    private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
        // 如果没有可用节点侧返回空
        if (instances.isEmpty()) {
            log.warn("No servers available for service: {}", serviceId);
            return new EmptyResponse();
        }

        // 利用AtomicInteger的自增(保证多线程下的原子性)，返回+1的计数器，实现轮询
        int pos = Math.abs(this.position.incrementAndGet());
        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }
}
