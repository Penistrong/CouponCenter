package org.penistrong.coupon.customer.event;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.service.intf.CouponCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
public class CouponConsumer {
    // 按照约定大于配置规则，函数式编程规范
    // 消费者的消费方法要和消息里的Function Name和Binding Name一致
    // 消息驱动框架默认使用消费者方法的MethodName作为当前消费者标识
    // 如果不一致，Spring就不知道将当前消费者Bean绑定到哪一个Stream信道上

    @Autowired
    private CouponCustomerService customerService;

    // 函数式编程方式之一
    @Bean
    public Consumer<RequestCoupon> addCoupon() {
        return request -> {
            log.info("Received addCoupon event: {}", request);
            customerService.requestCoupon(request);
        };
    }

    @Bean
    public Consumer<RequestCoupon> addCouponDelay() {
        return request -> {
            log.info("Received addCouponDelay event: {}", request);
            customerService.requestCoupon(request);
        };
    }

    @Bean
    public Consumer<String> deleteCoupon() {
        return request -> {
            log.info("Received deleteCoupon event: {}", request);
            // 将消息里携带的参数分开，创造消息时使用定界符','分隔并形成长串
            List<Long> params = Arrays.stream(request.split(","))
                    .map(Long::valueOf)
                    .toList();
            customerService.deleteCoupon(params.get(0), params.get(1));
        };
    }
}
