package org.penistrong.coupon.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.penistrong"})
@EnableDiscoveryClient
public class CouponCalculationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponCalculationApplication.class, args);
    }
}
