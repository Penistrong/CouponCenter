package org.penistrong.coupon.customer.event;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CouponProducer {

    @Autowired
    private StreamBridge streamBridge;

    public void produceRequestCouponEvent(RequestCoupon coupon) {
        log.info("Send request coupon event to MQ: userId={} want to request coupon={}",
                coupon.getUserId(),
                coupon);
        // 利用Stream原生组件，将该消息和实际携带的object发送给RabbitMQ中间件
        streamBridge.send(EventConstant.ADD_COUPON_EVENT, coupon);
    }

    public void produceRequestCouponDelayEvent(RequestCoupon coupon) {
        log.info("Send Delayed request coupon event to MQ: userId={} want to request coupon={}",
                coupon.getUserId(),
                coupon);
        streamBridge.send(EventConstant.ADD_COUPON_DELAY_EVENT,
                MessageBuilder.withPayload(coupon)
                        .setHeader("x-delay", 10 * 1000)
                        .build());
    }

    public void produceDeleteCouponEvent(Long userId, Long couponId) {
        log.info("Send delete coupon event to MQ: userId={}, couponId={}", userId, couponId);
        streamBridge.send(EventConstant.DELETE_COUPON_EVENT, userId + "," + couponId);
    }
}
