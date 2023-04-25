package org.penistrong.coupon.gateway.dynamic;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 不使用@RefreshScope获取Nacos动态参数
 * 通过注册监听器(实现Nacos::Listener接口)，监听Nacos Config的配置变化统治
 */
@Slf4j
@Component
public class DynamicRoutesListener implements Listener {

    @Autowired
    private GatewayService gatewayService;

    @Override
    public Executor getExecutor() {
        log.info("getExecutor");
        return null;
    }

    // 每当监听的Nacos配置文件发生变化，监听器便会调用receiveConfigInfo执行自定义逻辑
    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("Received routes config changes: {}", configInfo);

        // pom.xml中需要显式引用jakarta.validation-api.3.x.jar，版本为3.x优先
        // 否则Gateway项目下使用的jakarta.validation的类无法被解析
        // 因为默认的Spring-Cloud-Gateway-Starter:4.0.1版本下底层依赖的是
        // spring-boot-starter-validation:3.0.0 -> hibernate-validator:8.0.0 -> jakarta.validation-api:2.0.2
        List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
        // 调用GatewayService更新路由表
        gatewayService.updateRoutes(definitionList);
    }
}
