package com.skybox.annotation;

import com.skybox.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.annotation
 * @ClassName: VerifyParam
 * @Datetime: 2023/11/11 20:13
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于标识一个方法参数或字段需要进行校验的操作
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface VerifyParam {
    // 参数或字段的最小值
    int min() default -1;

    // 参数或字段的最大值
    int max() default -1;

    // 参数或字段是否为必需的
    boolean required() default false;

    // 参数或字段的校验正则表达式
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

}

