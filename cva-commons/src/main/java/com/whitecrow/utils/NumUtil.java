package com.whitecrow.utils;

import org.springframework.stereotype.Component;

@Component
public class NumUtil {
    public String blogNum(Long number) {
        if (number < 10000) {
            return String.valueOf(number);
        } else {
            double result = (double) number / 10000;
            return String.format("%.1fw", result);
        }
    }
    public Long reverseBlogNum(String value) {
        if (value.matches("\\d+")) { // 如果是数字字符串
            return Long.parseLong(value);
        } else if (value.matches("\\d+\\.\\d+w")) { // 如果是以"X.Xw"格式表示的数字字符串
            double result = Double.parseDouble(value.replace("w", ""));
            return (long) (result * 10000);
        } else {
            throw new IllegalArgumentException("Invalid input format");
        }
    }
}
