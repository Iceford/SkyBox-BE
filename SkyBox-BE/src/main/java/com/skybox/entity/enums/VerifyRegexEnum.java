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
    NO("", "不校验"), EMAIL("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$", "邮箱"), PASSWORD("^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,}$", "只能是数字，字母，特殊字符 8-18位");

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

