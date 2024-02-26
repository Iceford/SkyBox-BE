package com.skybox.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.mappers
 * @ClassName: EmailCodeMapper
 * @Datetime: 2023/11/11 20:48
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 针对邮箱验证码的数据库操作接口
 */

public interface EmailCodeMapper<T, P> extends BaseMapper<T, P> {

    /**
     * @param t     要更新的对象
     * @param email 邮箱
     * @param code  验证码
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据邮箱和验证码更新数据库记录
     */
    Integer updateByEmailAndCode(@Param("bean") T t, @Param("email") String email, @Param("code") String code);

    /**
     * @param null
     * @return null
     * @description 根据邮箱和验证码删除数据库记录
     */
    Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * @param email 邮箱
     * @param code  验证码
     * @return T 一个泛型类型 T 的对象
     * @description 根据邮箱和验证码查询数据库记录并返回对象
     */
    T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * @param email 邮箱
     * @return void
     * @description 禁用指定邮箱的验证码
     */
    void disableEmailCode(@Param("email") String email);

}

