package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: VerifyRegexEnum
 * @Datetime: 2023/11/11 18:29
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示正则校验的规则
 */

public enum VerifyRegexEnum {
    NO("", "不校验"),
    IP("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}", "IP地址"),
    POSITIVE_INTEGER("^[0-9]*[1-9][0-9]*$", "正整数"),
    NUMBER_LETTER_UNDER_LINE("^\\w+$", "由数字、26个英文字母或者下划线组成的字符串"),
    EMAIL("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$", "邮箱"),
    PHONE("(1[0-9])\\d{9}$", "手机号码"),
    COMMON("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", "数字，字母，中文，下划线"),
    PASSWORD("^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,}$", "只能是数字，字母，特殊字符 8-18位"),
    ACCOUNT("^[0-9a-zA-Z_]{1,}$", "字母开头,由数字、英文字母或者下划线组成"),
    MONEY("^[0-9]+(.[0-9]{1,2})?$", "金额");

    private final String regex;
    private final String desc;

    VerifyRegexEnum(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

    /**
     * @param
     * @return String
     * @description 获取正则表达式
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @param
     * @return String
     * @description 获取对应的描述
     */
    public String getDesc() {
        return desc;
    }
}

