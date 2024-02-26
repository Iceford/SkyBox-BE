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
     * 根据条件查询列表
     */
    List<UserInfo> findListByParam(UserInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(UserInfoQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

    /**
     * 新增
     */
    Integer add(UserInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<UserInfo> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<UserInfo> listBean);

    /**
     * 根据UserId查询对象
     */
    UserInfo getUserInfoByUserId(String userId);


    /**
     * 根据UserId修改
     */
    Integer updateUserInfoByUserId(UserInfo bean, String userId);


    /**
     * 根据UserId删除
     */
    Integer deleteUserInfoByUserId(String userId);


    /**
     * 根据Email查询对象
     */
    UserInfo getUserInfoByEmail(String email);


    /**
     * 根据Email修改
     */
    Integer updateUserInfoByEmail(UserInfo bean, String email);


    /**
     * 根据Email删除
     */
    Integer deleteUserInfoByEmail(String email);


    /**
     * 根据NickName查询对象
     */
    UserInfo getUserInfoByNickName(String nickName);


    /**
     * 根据NickName修改
     */
    Integer updateUserInfoByNickName(UserInfo bean, String nickName);


    /**
     * 根据NickName删除
     */
    Integer deleteUserInfoByNickName(String nickName);


    /**
     * 根据QqOpenId查询对象
     */
    UserInfo getUserInfoByQqOpenId(String qqOpenId);


    /**
     * 根据QqOpenId修改
     */
    Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId);


    /**
     * 根据QqOpenId删除
     */
    Integer deleteUserInfoByQqOpenId(String qqOpenId);


    /**
     * 登录
     */
    SessionWebUserDto login(String email, String password);

    /**
     * 注册
     */
    void register(String email, String nickName, String password, String emailCode);

    /**
     * 重设密码
     */
    void resetPwd(String email, String password, String emailCode);

    /**
     * 更新用户状态
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * qq登录
     */
    SessionWebUserDto qqLogin(String code);

    /**
     * 修改用户空间
     */
    void changeUserSpace(String userId, Integer changeSpace);
}
