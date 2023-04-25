package org.penistrong.coupon.gateway.dynamic;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 从Nacos加载配置项，包含两个场景
 * 1. 网关服务刚启动时，从Nacos读取配置项初始化路由表
 * 2. Nacos配置项发生变化，动态加载变化后的配置项
 * 为了满足场景1
 * 实现了InitializingBean接口，在当前类初始化为bean后执行定义在afterPropertiesSet()中的自定义逻辑
 */
@Slf4j
@Configuration
public class DynamicRoutesLoader implements InitializingBean {

    @Autowired
    private NacosConfigManager configManager;

    @Autowired
    private NacosConfigProperties configProperties;

    @Autowired
    private DynamicRoutesListener dynamicRoutesListener;

    @Value(value = "${dynamic-routes-config-name}")
    private String ROUTES_CONFIG;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Bean创建后首次加载配置
        String routes = configManager.getConfigService().getConfig(
                ROUTES_CONFIG,
                configProperties.getGroup(),
                10000);
        // 这里还未注册监听器，所以手动调用receiveConfigInfo()方法
        dynamicRoutesListener.receiveConfigInfo(routes);

        // 开始注册监听器
        configManager.getConfigService().addListener(
                ROUTES_CONFIG,
                configProperties.getGroup(),
                dynamicRoutesListener);
    }
}
