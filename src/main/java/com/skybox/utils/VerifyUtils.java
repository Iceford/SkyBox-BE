package com.skybox.utils;

import com.skybox.entity.enums.VerifyRegexEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: VerifyUtils
 * @Datetime: 2023/11/12 21:28
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一些验证方法用于检查字符串是否符合指定的正则表达式
 */

public class VerifyUtils {
    /**
     * @param regex
     * @param value
     * @return boolean
     * @description 使用给定的正则表达式验证字符串
     */
    public static boolean verify(String regex, String value) {
        if (StringTools.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * @param regex
     * @param value
     * @return boolean
     * @description 使用枚举类型中定义的正则表达式验证字符串
     */
    public static boolean verify(VerifyRegexEnum regex, String value) {
        return verify(regex.getRegex(), value);
    }
}

