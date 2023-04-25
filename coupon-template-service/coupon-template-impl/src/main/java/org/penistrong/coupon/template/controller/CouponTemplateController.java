package org.penistrong.coupon.template.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.penistrong.coupon.template.api.beans.CouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.PagedCouponTemplateInfo;
import org.penistrong.coupon.template.api.beans.TemplateSearchParams;
import org.penistrong.coupon.template.service.intf.CouponTemplateService;
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

    // 添加Sentinel注解，将Controller里请求的API也当作资源
    @GetMapping("/getTemplate")
    @SentinelResource(value = "getTemplate")
    public CouponTemplateInfo getTemplate(@RequestParam("id") Long id) {
        log.info("Query and load template info, id={}", id);
        return couponTemplateService.loadTemplateInfo(id);
    }

    // 降级逻辑需要尽可能地返回一个可被调用者服务处理的默认值，类似静默逻辑
    // 比如批量获取CouponTemplates时发生服务异常，应该返回一个空表，这样调用该接口的服务得到返回值后可以静默处理
    @GetMapping("/getBatchTemplates")
    @SentinelResource(value = "getBatchTemplates",
                      blockHandler = "getTemplateInBatch_block",
                      fallback = "getTemplateInBatch_fallback")
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("Get Templates In Batch: {}", JSON.toJSONString(ids));
        // 测试Sentinel熔断规则: 采取 异常比例 作为熔断策略
        // 在10s的滑动统计窗口内，如果发生异常的请求比例超过60%(0.6)，且最小总请求数为5，则开启5s的熔断策略
        if (ids.size() == 4)
            throw new RuntimeException("批量查询券模板，模板id个数等于4时抛出运行时异常，测试Sentinel熔断策略");
        return couponTemplateService.getTemplateInfoMap(ids);
    }

    // Sentinel配置getBatchTemplates接口的降级策略, blockHandler只能处理Sentinel抛出BlockException的情况
    public Map<Long, CouponTemplateInfo> getTemplateInBatch_block(Collection<Long> ids, BlockException e) {
        log.info("API::GetTemplateInBatch was blocked by Sentinel's BlockException (暂时降级)");
        return Maps.newHashMap();
    }

    // Sentinel配置getBatchTemplates接口的降级策略, fallback能处理其他RuntimeException
    // fallback的方法参数不需要接收BlockException, 否则无法处理其他异常的情况
    public Map<Long, CouponTemplateInfo> getTemplateInBatch_fallback(Collection<Long> ids) {
        log.info("API::GetTemplateInBatch was blocked by Other RuntimeException (暂时降级)");
        return Maps.newHashMap();
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
