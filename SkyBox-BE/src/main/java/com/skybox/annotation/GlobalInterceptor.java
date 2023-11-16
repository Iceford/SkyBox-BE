package com.skybox.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.annotation
 * @ClassName: GlobalInterceptor
 * @Datetime: 2023/11/11 20:02
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于标识一个方法需要进行全局拦截的操作
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {

    // 表示是否需要校验参数，默认为 false。如果设置为 true，则在方法执行前会进行参数校验
    boolean checkParams() default false;

    // 表示是否需要校验登录，默认为 true。如果设置为 true，则在方法执行前会进行登录校验
    boolean checkLogin() default true;

    // 表示是否需要校验管理员，默认为 false。如果设置为 true，则在方法执行前会进行管理员校验
    boolean checkAdmin() default false;

}

