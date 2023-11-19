package com.skybox.service.impl;

import com.skybox.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.SysSettingsDto;
import com.skybox.entity.dto.UserSpaceDto;
import com.skybox.entity.enums.PageSize;
import com.skybox.entity.enums.UserStatusEnum;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.query.SimplePage;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.exception.BusinessException;
import com.skybox.mappers.FileInfoMapper;
import com.skybox.mappers.UserInfoMapper;
import com.skybox.service.EmailCodeService;
import com.skybox.service.FileInfoService;
import com.skybox.service.UserInfoService;
import com.skybox.utils.RedisComponent;
import com.skybox.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: UserInfoServiceImpl
 * @Datetime: 2023/11/11 21:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 处理用户信息相关的业务逻辑
 */

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private EmailCodeService emailCodeService;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * @param email     邮箱账号
     * @param nickName  昵称
     * @param password  密码
     * @param emailCode 邮箱验证码
     * @return void
     * @description 注册
     */
    @Override
    @Transactional
    public void register(String email, String nickName, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null != userInfo) {
            throw new BusinessException("邮箱账号已经存在");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
        if (null != nickNameUser) {
            throw new BusinessException("昵称已经存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(email, emailCode);
        // 获取随机id
        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        // 根据输入的信息封装UserInfo对象
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        userInfo.setEmail(email);
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        // 从redis中取出系统设置
        SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
        // 用户空间
        userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace() * Constants.MB);
        userInfo.setUseSpace(fileInfoMapper.selectUseSpace(userId));
        this.userInfoMapper.insert(userInfo);
    }

    /**
     * @param email    邮箱账号
     * @param password 密码
     * @return SessionWebUserDto
     * @description 登录
     */
    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或者密码错误");
        }
        // 如果账户被禁用
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        // 根据id更新用户最后一次操作时间
        String userId = userInfo.getUserId();
        userInfoMapper.updateByUserId(updateInfo, userId);
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userId);
        sessionWebUserDto.setIsAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), email));
        // 用户空间
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        userSpaceDto.setUseSpace(fileInfoMapper.selectUseSpace(userId));
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userId, userSpaceDto);
        return sessionWebUserDto;

    }

    /**
     * @param email     邮箱账号
     * @param password  新密码
     * @param emailCode 邮箱验证码
     * @return void
     * @description 重置密码
     */
    @Override
    @Transactional
    public void resetPwd(String email, String password, String emailCode) {
        // 判断邮箱账号是否存在
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("邮箱账号不存在");
        }
        // 判断邮箱验证码是否正确
        emailCodeService.checkCode(email, emailCode);
        // 邮箱验证码正确可重置密码
        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringTools.encodeByMD5(password));
        userInfoMapper.updateByEmail(updateInfo, email);

    }

    /**
     * @param bean   UserInfo对象
     * @param userId 用户ID
     * @return void
     * @description 更新用户信息
     */
    @Override
    public void updateUserInfoByUserId(UserInfo bean, String userId) {
        userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * @param userId 用户ID
     * @return UserInfo
     * @description 获取用户信息
     */
    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * @param param UserInfoQuery对象
     * @return PaginationResultVO<UserInfo>
     * @description 分页查询用户列表
     */
    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(param);
        PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * @param param UserInfoQuery对象
     * @return Integer
     * @description 查询用户数量
     */
    @Override
    public Integer findCountByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectCount(param);
    }

    /**
     * @param param UserInfoQuery对象
     * @return List<UserInfo>
     * @description 查询用户列表
     */
    @Override
    public List<UserInfo> findListByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectList(param);
    }

    /**
     * @param userId 用户ID
     * @param status 状态
     * @return void
     * @description 更新用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(status);
        // 根据用户ID查询用户邮箱
        String email = userInfoMapper.selectByUserId(userId).getEmail();
        // 判断邮箱是否为管理员邮箱
        if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)) {
            // 如果是管理员邮箱则抛出一个自定义异常"不可更改管理员状态"
            throw new BusinessException("不可更改管理员状态");
        }
        // 判断是否禁用用户账号
        if (UserStatusEnum.DISABLE.getStatus().equals(status)) {
            // 如果禁用则将用户已使用空间设置为0，并调用fileInfoService的deleteFileByUserId方法删除用户的文件信息
            userInfo.setUseSpace(0L);
            fileInfoService.deleteFileByUserId(userId);
        }
        // 更新用户状态
        userInfoMapper.updateByUserId(userInfo, userId);
    }

    /**
     * @param userId      用户ID
     * @param changeSpace 空间大小
     * @return void
     * @description 更改用户空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserSpace(String userId, Integer changeSpace) {
        Long space = changeSpace * Constants.MB;
        this.userInfoMapper.updateUserSpace(userId, null, space);
        redisComponent.resetUserSpaceUse(userId);
    }
}

