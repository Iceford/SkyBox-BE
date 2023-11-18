package com.skybox.service;

import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service
 * @ClassName: UserInfoService
 * @Datetime: 2023/11/11 21:08
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于定义用户信息相关的业务逻辑方法
 */

public interface UserInfoService {
    /**
     * @param email 用户的邮箱
     * @param nickName 用户的昵称
     * @param password 密码
     * @param emailCode 邮箱验证码
     * @return void
     * @description 用户注册的方法
     */
    void register(String email, String nickName, String password, String emailCode);

    /**
     * @param email 用户的邮箱
     * @param password 用户的密码
     * @return SessionWebUserDto 登录成功后的用户信息
     * @description 用户登录的方法
     */
    SessionWebUserDto login(String email, String password);

    /**
     * @param email 用户的邮箱
     * @param password 新密码
     * @param emailCode 邮箱验证码
     * @return void
     * @description 重置用户密码的方法
     */
    void resetPwd(String email, String password, String emailCode);

    /**
     * @param userInfo 要更新的用户信息对象
     * @param userId 用户ID
     * @return void
     * @description 根据用户ID更新用户信息的方法
     */
    void updateUserInfoByUserId(UserInfo userInfo, String userId);

    /**
     * @param userId 用户ID
     * @return UserInfo 对应用户ID的用户信息对象
     * @description 根据用户ID获取用户信息的方法
     */
    UserInfo getUserInfoByUserId(String userId);

    /**
     * @param userInfoQuery 用户信息查询参数对象
     * @return PaginationResultVO 分页查询结果的用户列表
     * @description 分页查询用户列表的方法
     */
    PaginationResultVO findListByPage(UserInfoQuery userInfoQuery);

    /**
     * @param param 用户信息查询参数对象
     * @return Integer 符合参数条件的用户数量
     * @description 根据参数查询用户数量的方法
     */
    Integer findCountByParam(UserInfoQuery param);

    /**
     * @param param 用户信息查询参数对象
     * @return List<UserInfo> 符合参数条件的用户列表
     * @description 根据参数查询用户列表的方法
     */
    List<UserInfo> findListByParam(UserInfoQuery param);

    /**
     * @param userId 用户ID
     * @param status 要更新的用户状态
     * @return void
     * @description 更新用户状态的方法
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * @param userId 用户ID
     * @param changeSpace 要变更的空间大小
     * @return void
     * @description 修改用户空间大小的方法
     */
    void changeUserSpace(String userId, Integer changeSpace);

}

