package org.penistrong.coupon.template.dao.converter;

import com.alibaba.fastjson.JSON;
import org.penistrong.coupon.template.api.beans.rules.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RuleConverter implements AttributeConverter<TemplateRule, String> {

    @Override
    public String convertToDatabaseColumn(TemplateRule templateRule) {
        return JSON.toJSONString(templateRule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String ruleStr) {
        return JSON.parseObject(ruleStr, TemplateRule.class);
    }
}