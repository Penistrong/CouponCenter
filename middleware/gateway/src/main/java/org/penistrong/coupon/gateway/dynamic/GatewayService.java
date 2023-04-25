package org.penistrong.coupon.gateway.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 注册到Nacos配置中心，注意服务发现已经在application.yml中配置过了
 */
@Slf4j
@Service
public class GatewayService {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private ApplicationEventPublisher publisher;

    // List<RouteDefinition>是Gateway网关组件封装路由规则的标准类
    // 使用内置的RouteDefinitionWriter将路由规则写入上下文，再调用事件发布器发布路由刷新事件
    public void updateRoutes(List<RouteDefinition> routes) {
        if (CollectionUtils.isEmpty(routes)) {
            log.info("No Route Definition Found...Skip updating routes");
            return;
        }

        routes.forEach(r -> {
            try{
                routeDefinitionWriter.save(Mono.just(r)).subscribe();
                // 高版本Spring-Cloud-Dependencies下使用这个方法触发广播会导致卡死
                // publisher.publishEvent(new RefreshRoutesEvent(this));
            } catch (Exception e) {
                log.error("Cannot update route config, id = {}", r.getId());
            }
        });
    }
}
