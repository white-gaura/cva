package com.whitecrow.utils;


import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

public class BeanCopyUtil {
    /**
     * @param sourceObj(源对象)
     * @param targetObj(目标对象)
     */
    public static void beanCopy(Object sourceObj, Object targetObj) {
        // 创建 ConversionService
        DefaultConversionService conversionService = new DefaultConversionService();

        // 注册自定义 Converter，直接返回源值
        conversionService.addConverter(new Converter<Object, Object>() {
            @Override
            public Object convert(Object source) {
                return source;
            }
        });
        BeanUtils.copyProperties(sourceObj, targetObj, null, "conversionService");

    }

}