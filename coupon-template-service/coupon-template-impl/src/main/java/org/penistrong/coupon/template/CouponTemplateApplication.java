package org.penistrong.coupon.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// ComponentScan这个注解其实可以去掉，因为sub-module的代码都是在org.penistrong.coupon.template下面
// 启动类也位于该包下，不需要再定义基础包名去别的模块下扫描
@SpringBootApplication
// 未拆分成独立服务前，以下注解只能出现1次，所以先注释掉以让ConsumerApplication三合一启动
@EnableJpaAuditing
@ComponentScan(basePackages = {"org.penistrong"})
// 单应用启动时扫描不到couponTemplateDAO，通过这种方式添加注解配置(其实是因为包不对，不小心放在template的子模块下面了)
// @EntityScan(basePackages = {"org.penistrong.coupon.template.dao.entity"})
// @EnableJpaRepositories(basePackages = {"org.penistrong.coupon.template.dao"})
// @EnableDiscoveryClient 是让多个实例能够被发现
@EnableDiscoveryClient
public class CouponTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class, args);
    }
}
