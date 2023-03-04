package org.penistrong.coupon.template.impl.service.intf;

import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.TemplateSearchParams;

import java.util.Collection;
import java.util.Map;

public interface CouponTemplateService {

    // 创建优惠券模板
    CouponTemplateInfo createTemplate(CouponTemplateInfo request);

    // 克隆优惠券模板(比如克隆unavailable的券模板为新的available的券模板)
    CouponTemplateInfo cloneTemplate(Long templateId);

    // 分页模板查询
    PagedCouponTemplateInfo search(TemplateSearchParams request);

    // 给定模板ID查询优惠券模板
    CouponTemplateInfo loadTemplateInfo(Long id);

    // 删除优惠券模板
    void deleteTemplate(Long id);

    //根据给定的一组模板ID批量查询模板信息，返回Map<TemplateId, TemplateInfo>
    Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids);
}
