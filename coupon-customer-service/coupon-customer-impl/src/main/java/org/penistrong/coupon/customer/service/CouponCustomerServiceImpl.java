package org.penistrong.coupon.customer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.penistrong.coupon.calculation.api.beans.ShoppingCart;
import org.penistrong.coupon.calculation.api.beans.SimulationOrder;
import org.penistrong.coupon.calculation.api.beans.SimulationResponse;
import org.penistrong.coupon.calculation.service.intf.CouponCalculationService;
import org.penistrong.coupon.customer.api.beans.RequestCoupon;
import org.penistrong.coupon.customer.api.beans.CouponSearchParams;
import org.penistrong.coupon.customer.api.enums.CouponStatus;
import org.penistrong.coupon.customer.dao.CouponDao;
import org.penistrong.coupon.customer.dao.entity.Coupon;
import org.penistrong.coupon.customer.service.intf.CouponCustomerService;
import org.penistrong.coupon.template.api.beans.CouponInfo;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponInfo;
import org.penistrong.coupon.template.service.intf.CouponTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    // 将template模块和calculation模块的service类注入过来以方便使用
    @Autowired
    private CouponTemplateService templateService;

    @Autowired
    private CouponCalculationService calculationService;

    @Override
    public Coupon requestCoupon(RequestCoupon request) {
        CouponTemplateInfo templateInfo = templateService.loadTemplateInfo(request.getCouponTemplateId());

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
                .build();
        couponDao.save(coupon);

        return coupon;
    }

    @Override
    public ShoppingCart placeOrder(ShoppingCart cart) {
        return null;
    }

    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {
        return null;
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

        // Coupon::templateinfo是@Transient的，没有持久化到表中
        // 还需调用templateService去查找
        List<Long> templateIds = coupons.stream()
                .map(Coupon::getTemplateId)
                .toList();

        Map<Long, CouponTemplateInfo> templateInfoMap= templateService.getTemplateInfoMap(templateIds);

        coupons.forEach(c -> c.setTemplateInfo(templateInfoMap.get(c.getTemplateId())));

        List<CouponInfo> couponInfos = coupons.stream()
                .map(CouponConverter::convertToCoupon)
                .toList();

        PagedCouponInfo response = PagedCouponInfo.builder()
                .couponInfos(couponInfos)
                .page(request.getPage())
                .total(coupons.getTotalElements())
                .build();

        return response;
    }
}
