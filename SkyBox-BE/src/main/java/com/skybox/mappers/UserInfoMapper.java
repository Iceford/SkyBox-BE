package com.skybox.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.mappers
 * @ClassName: UserInfoMapper
 * @Datetime: 2023/11/11 20:52
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 针对用户信息的数据库操作接口
 */

public interface UserInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * @param t      要更新的对象
     * @param userId 用户ID
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据用户ID更新数据库记录
     */
    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

    /**
     * @param userId 用户ID
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据用户ID删除数据库记录
     */
    Integer deleteByUserId(@Param("userId") String userId);

    /**
     * @param userId 用户ID
     * @return T 一个泛型类型 T 的对象
     * @description 根据用户ID查询数据库记录并返回对
     */
    T selectByUserId(@Param("userId") String userId);

    /**
     * @param t     要更新的对象
     * @param email 电子邮件
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据电子邮件更新数据库记录
     */
    Integer updateByEmail(@Param("bean") T t, @Param("email") String email);

    /**
     * @param email 电子邮件
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据电子邮件删除数据库记录
     */
    Integer deleteByEmail(@Param("email") String email);

    /**
     * @param email 电子邮件
     * @return T 一个泛型类型 T 的对象
     * @description 根据电子邮件查询数据库记录并返回对象
     */
    T selectByEmail(@Param("email") String email);

    /**
     * @param t        根据昵称更新数据库记录
     * @param nickName 昵称
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据昵称更新数据库记录
     */
    Integer updateByNickName(@Param("bean") T t, @Param("nickName") String nickName);

    /**
     * @param nickName 昵称
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据昵称删除数据库记录
     */
    Integer deleteByNickName(@Param("nickName") String nickName);

    /**
     * @param nickName 昵称
     * @return T 一个泛型类型 T 的对象
     * @description 根据昵称查询数据库记录并返回对象
     */
    T selectByNickName(@Param("nickName") String nickName);

    /**
     * @param t        要更新的对象
     * @param qqOpenId QQ开放ID
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据QQ开放ID更新数据库记录
     */
    Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);

    /**
     * @param qqOpenId QQ开放ID
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据QQ开放ID删除数据库记录
     */
    Integer deleteByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * @param qqOpenId QQ开放ID
     * @return T 一个泛型类型 T 的对象
     * @description 根据QQ开放ID查询数据库记录并返回对象
     */
    T selectByQqOpenId(@Param("qqOpenId") String qqOpenId);

    /**
     * @param userId     用户ID
     * @param useSpace   已使用的存储空间
     * @param totalSpace 总存储空间
     * @return Integer 一个整数，表示更新的记录数
     * @description 更新用户的存储空间信息
     */
    Integer updateUserSpace(@Param("userId") String userId, @Param("useSpace") Long useSpace, @Param("totalSpace") Long totalSpace);

}

