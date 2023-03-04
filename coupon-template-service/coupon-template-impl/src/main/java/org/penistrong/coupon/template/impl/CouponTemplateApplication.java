package org.penistrong.coupon.template.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// ComponentScan这个注解其实可以去掉，因为sub-module的代码都是在org.penistrong.coupon.template下面
// 启动类也位于该包下，不需要再定义基础包名去别的模块下扫描
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"org.penistrong"})
// 单应用启动时扫描不到couponTemplateDAO，通过这种方式添加注解配置
@EntityScan(basePackages = {"org.penistrong.coupon.template.dao.entity"})
@EnableJpaRepositories(basePackages = {"org.penistrong.coupon.template.dao"})
public class CouponTemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateApplication.class, args);
    }
}
