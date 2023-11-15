package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: DateTimePatternEnum
 * @Datetime: 2023/11/11 18:16
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示日期时间格式的模式
 */

public enum DateTimePatternEnum {
    // 年份-月份-日期 小时:分钟:秒、年份-月份-日期、年份和月份
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYY_MM_DD("yyyy-MM-dd"), YYYYMM("yyyyMM");

    private String pattern;

    DateTimePatternEnum(String pattern) {
        this.pattern = pattern;
    }

    // 获取每个枚举常量对应的模式字符串
    public String getPattern() {
        return pattern;
    }
}

