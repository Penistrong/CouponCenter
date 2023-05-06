# 优惠券计算平台-微服务架构

## 概览

1. Nacos: 注册中心+配置中心
2. OpenFeign: 微服务间RESTful风格的远程调用
3. Spring Cloud Gateway: 微服务网关，粗粒度限流、过滤
4. Sentinel: 细粒度服务限流、降级、熔断
5. Micrometer-Tracing + Zipkin: 微服务链路追踪(Spring Boot 3.0之后Sleuth移入Micrometer-Tracing项目下)
6. Seata: 分布式事务，配合Nacos实现2PC的AT模式
7. RabbitMQ: 消息队列适配，解耦微服务间的直接调用、削峰填谷、利用延迟消息交换机插件实现延迟消息
