package org.penistrong.coupon.customer.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.api.beans.CouponSearchParams;
import org.penistrong.coupon.customer.api.enums.CouponStatus;
import org.penistrong.coupon.customer.dao.CouponDao;
import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.customer.feign.CalculationService;
import org.penistrong.coupon.customer.feign.TemplateService;
import org.penistrong.coupon.customer.service.intf.CouponCustomerService;
import org.penistrong.coupon.template.api.beans.CouponInfo;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.penistrong.coupon.customer.constant.Constant.TRAFFIC_VERSION;

@Slf4j
@Service
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private WebClient.Builder webClientBuilder;

    // 注入OpenFeign托管的远程服务调用接口
    @Autowired
    private TemplateService templateService;

    @Autowired
    private CalculationService calculationService;

    @Override
    public Coupon requestCoupon(RequestCoupon request) {
        /* get: 指明Http Method为GET
         * uri: 指明远程调用的请求地址，Nacos会使用服务发现机制根据目标服务名称解析为可用服务节点
         * retrieve: 直接获取responseBody并准备decode
         * bodyToMono: 将responseBody解析并转换为具体的Java对象
         * block: 将类型为T的对象从Mono<T>中取出
         */
        CouponTemplateInfo templateInfo = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-service/template/getTemplate?id="+request.getCouponTemplateId())
                // LoadBalancer - 测试流量打标，将trafficVersion传入WebClient的Header中
                .header(TRAFFIC_VERSION, request.getTrafficVersion())
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();

        // 若不存在模板则抛出异常
        if (templateInfo == null){
            log.error("Invalid template_id={}", request.getCouponTemplateId());
            throw new IllegalArgumentException("Invalid template id");
        }

        // 模板也不能处于失效状态
        long now = Calendar.getInstance().getTimeInMillis();
        Long expireTime = templateInfo.getRule().getDeadline();
        if (expireTime != null && now > expireTime || BooleanUtils.isFalse(templateInfo.isAvailable())) {
            log.error("Template [id={}] is not available", request.getCouponTemplateId());
            throw new IllegalArgumentException("Unavailable template to request");
        }

        // 用户领券不能超过拥有该券的上限
        long count = couponDao.countByUserIdAndTemplateId(request.getUserId(), request.getCouponTemplateId());
        if (count >= templateInfo.getRule().getLimitations()) {
            log.error("This user[id={}] exceeds limitations of possessing the coupon[template_id={}]",
                    request.getUserId(), request.getCouponTemplateId());
            throw new IllegalArgumentException("Coupon limitations exceed");
        }

        Coupon coupon = Coupon.builder()
                .templateId(request.getCouponTemplateId())
                .userId(request.getUserId())
                .shopId(templateInfo.getShopId())
                .status(CouponStatus.AVAILABLE)
                .templateInfo(templateInfo)
                .build();
        couponDao.save(coupon);

        return coupon;
    }

    @Override
    public ShoppingCart placeOrder(ShoppingCart cart) {
        if (CollectionUtils.isEmpty(cart.getProducts())) {
            log.error("Invalid check out request with empty cart, cart={}", cart);
            throw new IllegalArgumentException("Cart is empty");
        }

        Coupon coupon = null;
        if (cart.getCouponId() != null) {
            // 结算时使用里优惠券，验证其是否可用
            Coupon example = Coupon.builder()
                    .userId(cart.getUserId())
                    .id(cart.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();

            coupon = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            CouponInfo couponInfo = CouponConverter.convertToCouponInfo(coupon);
            couponInfo.setTemplate(loadTemplateInfo(couponInfo.getTemplateId()));

            cart.setCouponInfos(Lists.newArrayList(couponInfo));
        }
        // 结算购物车
        /* 改造为OpenFeign接口调用
        ShoppingCart checkoutInfo = webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-service/calculator/checkout")
                .bodyValue(cart)
                .retrieve()
                .bodyToMono(ShoppingCart.class)
                .block();
         */

        ShoppingCart checkoutInfo = calculationService.checkout(cart);

        if (coupon == null) {
            // 如果优惠券没有被结算，而用户使用里优惠券，则报错提示满足不了优惠券的使用条件
            if (CollectionUtils.isEmpty(checkoutInfo.getCouponInfos())) {
                log.error("Cannot apply coupon to cart, couponId={}", coupon.getId());
                throw new IllegalArgumentException("Coupon is not applicable on this cart");
            }

            log.info("Update coupon status to used, couponId={}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
            couponDao.save(coupon);
        }

        return checkoutInfo;
    }

    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {
        List<CouponInfo> couponInfos = Lists.newArrayList();
        // 循环加载优惠券信息，然而最好采用批量查询，以免高并发导致问题
        // 由于券模板一旦创建后不会对其执行修改，创建完毕后可以将数据异构放入缓存，从缓存中直接取用
        for (Long couponId : order.getCouponIDs()) {
            Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(couponId)
                    .status(CouponStatus.AVAILABLE)
                    .build();
            Optional<Coupon> couponOptional = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst();
            // 加载模板信息
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                CouponInfo couponInfo = CouponConverter.convertToCouponInfo(coupon);

                couponInfo.setTemplate(loadTemplateInfo(coupon.getTemplateId()));
                couponInfos.add(couponInfo);
            }
        }
        order.setCouponInfos(couponInfos);

        /* 改造为OpenFeign接口调用
        return webClientBuilder.build()
                .post()
                .uri("http://coupon-calculation-service/calculator/simulate")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(SimulationResponse.class)
                .block();
         */
        return calculationService.simulate(order);
    }

    // 根据给定模板id调用template-service获得其具体模板信息
    private CouponTemplateInfo loadTemplateInfo(Long templateId) {
        /*
        return webClientBuilder.build()
                .get()
                .uri("http://coupon-template-service/template/getTemplate?id=" + templateId)
                .retrieve()
                .bodyToMono(CouponTemplateInfo.class)
                .block();
         */
        // 改造为OpenFeign调用远程服务，而不是直接用WebClient调用
        return templateService.getTemplate(templateId);
    }

    @Override
    public void deleteCoupon(Long userId, Long couponId) {
        Coupon example = Coupon.builder()
                .userId(userId)
                .id(couponId)
                .status(CouponStatus.AVAILABLE)
                .build();
        Coupon coupon = couponDao.findAll(Example.of(example))
                .stream()
                .findFirst()
                // 表中找不到userId和couponId的对应行
                .orElseThrow(() -> new RuntimeException("Could not find available coupon"));

        coupon.setStatus(CouponStatus.INACTIVE);
        couponDao.save(coupon);
    }

    /**
     * 用户查询其拥有的优惠券信息, 返回分页查询信息
     *
     * @param request
     * @return CouponInfoList
     */
    @Override
    public PagedCouponInfo searchCoupons(CouponSearchParams request) {
        Coupon example = Coupon.builder()
                .userId(request.getUserId())
                .status(CouponStatus.convert(request.getCouponStatusCode()))
                .shopId(request.getShopId())
                .build();

        Pageable page = PageRequest.of(request.getPage(), request.getPageSize());
        // DAO去数据库表"coupon"中执行批量查询
        Page<Coupon> coupons = couponDao.findAll(Example.of(example), page);

        /* 改造为OpenFeign接口调用

        // Coupon::templateinfo是@Transient的，没有持久化到表中
        // 还需调用templateService去查找
        // 从List<Long>改成以","分割的长String是为了给下面的get请求传值
        String templateIds = coupons.stream()
                .map(Coupon::getTemplateId)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.joining(","));

        // 构造ParameterizedTypeReference实例让WebClient将ResponseBody转换为对应类型
        Map<Long, CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
                .get()
                .uri("http://coupon-template-service/template/getBatchTemplates?ids="+templateIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, CouponTemplateInfo>>(){})
                .block();

        */
        List<Long> templateIds = coupons.stream()
                .map(Coupon::getTemplateId)
                .distinct()
                .toList();

        Map<Long, CouponTemplateInfo> templateInfoMap = templateService
                .getBatchTemplate(templateIds);

        coupons.forEach(c -> c.setTemplateInfo(templateInfoMap.get(c.getTemplateId())));

        List<CouponInfo> couponInfos = coupons.stream()
                .map(CouponConverter::convertToCouponInfo)
                .toList();

        PagedCouponInfo response = PagedCouponInfo.builder()
                .couponInfos(couponInfos)
                .page(request.getPage())
                .total(coupons.getTotalElements())
                .build();

        return response;
    }
}
