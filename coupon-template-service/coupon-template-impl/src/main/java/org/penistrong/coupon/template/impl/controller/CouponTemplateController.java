package org.penistrong.coupon.template.impl.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.TemplateSearchParams;
import org.penistrong.coupon.template.impl.service.intf.CouponTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateController {

    @Autowired
    private CouponTemplateService couponTemplateService;

    @PostMapping("/addTemplate")
    public CouponTemplateInfo addTemplate(@Valid @RequestBody CouponTemplateInfo request) {
        log.info("Create new coupon template: data={}", request);
        return couponTemplateService.createTemplate(request);
    }

    @PostMapping("/cloneTemplate")
    public CouponTemplateInfo cloneTemplate(@RequestParam("id") Long id) {
        log.info("Clone coupon template: data={}", id);
        return couponTemplateService.cloneTemplate(id);
    }

    @GetMapping("/getTemplate")
    public CouponTemplateInfo getTemplate(@RequestParam("id") Long id) {
        log.info("Query and load template info, id={}", id);
        return couponTemplateService.loadTemplateInfo(id);
    }

    @GetMapping("/getBatchTemplates")
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("Get Templates In Batch: {}", JSON.toJSONString(ids));
        return couponTemplateService.getTemplateInfoMap(ids);
    }

    @PostMapping("/search")
    public PagedCouponTemplateInfo search(@Valid @RequestBody TemplateSearchParams request) {
        log.info("Search templates by Params, payload={}", request);
        return couponTemplateService.search(request);
    }

    @DeleteMapping("/deleteTemplate")
    public void deleteTemplate(@RequestParam("id") Long id) {
        log.info("Preparing to load template, id={}", id);
        couponTemplateService.deleteTemplate(id);
    }

}
