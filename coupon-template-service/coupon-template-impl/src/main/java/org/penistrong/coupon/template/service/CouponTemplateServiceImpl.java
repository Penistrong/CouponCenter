package org.penistrong.coupon.template.service;

import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.TemplateSearchParams;
import org.penistrong.coupon.template.api.enums.CouponType;
import org.penistrong.coupon.template.dao.CouponTemplateDao;
import org.penistrong.coupon.template.dao.entity.CouponTemplate;
import org.penistrong.coupon.template.converter.CouponTemplateConverter;
import org.penistrong.coupon.template.service.intf.CouponTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {

    @Autowired
    private CouponTemplateDao templateDao;

    @Override
    public CouponTemplateInfo createTemplate(CouponTemplateInfo request) {
        // 单个门店最多可以创建100张优惠券模板
        if (request.getShopId() != null){
            Integer count = templateDao.countByShopIdAndAvailable(request.getShopId(), true);
            if (count >= 100) {
                log.error("Existed coupon template nums of current shop exceeded maximum limitation");
                throw new UnsupportedOperationException("Exceeded the maximum numbers of coupon templates");
            }
        }

        // 创建新优惠券
        CouponTemplate template = CouponTemplate.builder()
                .name(request.getName())
                .description(request.getDesc())
                .category(CouponType.convert(request.getType()))
                .available(true)
                .shopId(request.getShopId())
                .rule(request.getRule())
                .build();
        template = templateDao.save(template);

        // 返回response时使用converter将其转换为返回的DTO(在本模块中Controller直接返回该DTO作为VO)
        return CouponTemplateConverter.convertToTemplateInfo(template);
    }

    @Override
    public CouponTemplateInfo cloneTemplate(Long templateId) {
        log.info("Cloning template[id: {}]", templateId);
        CouponTemplate src = templateDao.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid template ID"));

        CouponTemplate tgt = new CouponTemplate();
        BeanUtils.copyProperties(src, tgt);

        // 克隆出的新模板，无论原模板是否可用，新模板必须可用
        tgt.setAvailable(true);
        // 使用DAO将该对象持久化到数据库里，由于Id是主键，需要置空以自动插入
        tgt.setId(null);

        templateDao.save(tgt);
        return CouponTemplateConverter.convertToTemplateInfo(tgt);
    }

    // 分页查询优惠券模板
    @Override
    public PagedCouponTemplateInfo search(TemplateSearchParams request) {
        CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(CouponType.convert(request.getType()))
                .available(request.getAvailable())
                .name(request.getName())
                .build();

        Pageable page = PageRequest.of(request.getPage(), request.getPageSize());
        // DAO去数据库批量查询
        // 该复杂查询使用的是Example对象，JPA会根据Example对象的有值字段作为查询条件进行查询
        Page<CouponTemplate> result = templateDao.findAll(Example.of(example), page);
        List<CouponTemplateInfo> couponTemplateInfos = result.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .toList();

        PagedCouponTemplateInfo response = PagedCouponTemplateInfo.builder()
                .templates(couponTemplateInfos)
                .page(request.getPage())
                .total(result.getTotalElements())
                .build();

        return response;
    }

    @Override
    public CouponTemplateInfo loadTemplateInfo(Long id) {
        Optional<CouponTemplate> template = templateDao.findById(id);
        return template.map(CouponTemplateConverter::convertToTemplateInfo).orElse(null);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        // 并不是真的如下所示的真删除，只是将其设定为unavailable
        // templateDao.deleteById(id);
        int rows = templateDao.makeCouponUnavailable(id);
        // 因为采取的是UPDATE的自定义SQL，不存在数据库表中不存在该id，那么更新的行数就为0
        if (rows == 0)
            throw new IllegalArgumentException("Template Not Found: [id]" + id);
    }

    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);

        // collect时使用Function.identity()直接返回每次处理的CouponTemplateInfo自身
        Map<Long, CouponTemplateInfo> infomap = templates.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toMap(CouponTemplateInfo::getId, Function.identity()));

        return infomap;
    }
}
