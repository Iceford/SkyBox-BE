package com.skybox.service;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: EmailCodeService
 * @Datetime: 2023/11/11 20:53
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于定义邮箱验证码的业务逻辑方法
 */

public interface EmailCodeService {
    /**
     * @param toEmail 目标邮箱地址
     * @param type 验证码类型
     * @return void
     * @description 向指定的邮箱发送验证码
     */
    void sendEmailCode(String toEmail, Integer type);

    /**
     * @param email 邮箱地址
     * @param code 待验证的验证码
     * @return void
     * @description 验证用户输入的验证码是否正确
     */
    void checkCode(String email, String code);
}

