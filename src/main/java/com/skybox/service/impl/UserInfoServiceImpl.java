package com.skybox.service.impl;

import com.skybox.component.RedisComponent;
import com.skybox.entity.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.QQInfoDto;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.SysSettingsDto;
import com.skybox.entity.dto.UserSpaceDto;
import com.skybox.entity.enums.PageSize;
import com.skybox.entity.enums.UserStatusEnum;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.SimplePage;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.exception.BusinessException;
import com.skybox.mappers.UserInfoMapper;
import com.skybox.service.EmailCodeService;
import com.skybox.service.FileInfoService;
import com.skybox.service.UserInfoService;
import com.skybox.utils.JsonUtils;
import com.skybox.utils.OKHttpUtils;
import com.skybox.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: UserInfoServiceImpl
 * @Datetime: 2023/11/11 21:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 处理用户信息相关的业务逻辑
 */

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private EmailCodeService emailCodeService;
    @Resource
    private FileInfoService fileInfoService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserInfo> findListByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserInfoQuery param) {
        return this.userInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
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
     * 新增
     */
    @Override
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据UserId获取对象
     */
    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    /**
     * 根据UserId修改
     */
    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    /**
     * 根据UserId删除
     */
    @Override
    public Integer deleteUserInfoByUserId(String userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }

    /**
     * 根据Email获取对象
     */
    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    /**
     * 根据Email修改
     */
    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    /**
     * 根据Email删除
     */
    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * 根据NickName获取对象
     */
    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return this.userInfoMapper.selectByNickName(nickName);
    }

    /**
     * 根据NickName修改
     */
    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return this.userInfoMapper.updateByNickName(bean, nickName);
    }

    /**
     * 根据NickName删除
     */
    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return this.userInfoMapper.deleteByNickName(nickName);
    }

    /**
     * 根据QqOpenId获取对象
     */
    @Override
    public UserInfo getUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.selectByQqOpenId(qqOpenId);
    }

    /**
     * 根据QqOpenId修改
     */
    @Override
    public Integer updateUserInfoByQqOpenId(UserInfo bean, String qqOpenId) {
        return this.userInfoMapper.updateByQqOpenId(bean, qqOpenId);
    }

    /**
     * 根据QqOpenId删除
     */
    @Override
    public Integer deleteUserInfoByQqOpenId(String qqOpenId) {
        return this.userInfoMapper.deleteByQqOpenId(qqOpenId);
    }

    /**
     * @param email
     * @param password
     * @return SessionWebUserDto
     * @description 通过提供的邮箱和密码进行用户登录验证
     */
    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null == userInfo || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或者密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());
        sessionWebUserDto.setAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), email));
        // 用户空间
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        userSpaceDto.setUseSpace(fileInfoService.getUserUseSpace(userInfo.getUserId()));
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
        return sessionWebUserDto;
    }

    /**
     * @param email
     * @param nickName
     * @param password
     * @param emailCode
     * @return void
     * @description 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        userInfo.setEmail(email);
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();
        userInfo.setTotalSpace(sysSettingsDto.getUserInitUseSpace() * Constants.MB);
        userInfo.setUseSpace(0L);
        this.userInfoMapper.insert(userInfo);
    }

    /**
     * @param email
     * @param password
     * @param emailCode
     * @return void
     * @description 重置用户密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null == userInfo) {
            throw new BusinessException("邮箱账号不存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(email, emailCode);

        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringTools.encodeByMD5(password));
        this.userInfoMapper.updateByEmail(updateInfo, email);
    }


    /**
     * @param userId
     * @param status
     * @return void
     * @description 新用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(status);
        if (UserStatusEnum.DISABLE.getStatus().equals(status)) {
            userInfo.setUseSpace(0L);
            fileInfoService.deleteFileByUserId(userId);
        }
        userInfoMapper.updateByUserId(userInfo, userId);
    }

    /**
     * @param code
     * @return SessionWebUserDto
     * @description 使用QQ账号进行登录
     */
    @Override
    public SessionWebUserDto qqLogin(String code) {
        String accessToken = getQQAccessToken(code);
        String openId = getQQOpenId(accessToken);
        UserInfo user = this.userInfoMapper.selectByQqOpenId(openId);
        String avatar = null;
        if (null == user) {
            QQInfoDto qqInfo = getQQUserInfo(accessToken, openId);
            user = new UserInfo();

            String nickName = qqInfo.getNickname();
            nickName = nickName.length() > Constants.LENGTH_150 ? nickName.substring(0, 150) : nickName;
            avatar = StringTools.isEmpty(qqInfo.getFigureurl_qq_2()) ? qqInfo.getFigureurl_qq_1() : qqInfo.getFigureurl_qq_2();
            Date curDate = new Date();

            // 上传头像到本地
            user.setQqOpenId(openId);
            user.setJoinTime(curDate);
            user.setNickName(nickName);
            user.setQqAvatar(avatar);
            user.setUserId(StringTools.getRandomString(Constants.LENGTH_10));
            user.setLastLoginTime(curDate);
            user.setStatus(UserStatusEnum.ENABLE.getStatus());
            user.setUseSpace(0L);
            user.setTotalSpace(redisComponent.getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            this.userInfoMapper.insert(user);
            user = userInfoMapper.selectByQqOpenId(openId);
        } else {
            UserInfo updateInfo = new UserInfo();
            updateInfo.setLastLoginTime(new Date());
            avatar = user.getQqAvatar();
            this.userInfoMapper.updateByQqOpenId(updateInfo, openId);
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(user.getStatus())) {
            throw new BusinessException("账号被禁用无法登录");
        }
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setUserId(user.getUserId());
        sessionWebUserDto.setNickName(user.getNickName());
        sessionWebUserDto.setAvatar(avatar);
        sessionWebUserDto.setAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), user.getEmail() == null ? "" : user.getEmail()));

        UserSpaceDto userSpaceDto = new UserSpaceDto();
        userSpaceDto.setUseSpace(fileInfoService.getUserUseSpace(user.getUserId()));
        userSpaceDto.setTotalSpace(user.getTotalSpace());
        redisComponent.saveUserSpaceUse(user.getUserId(), userSpaceDto);
        return sessionWebUserDto;
    }

    /**
     * @param code
     * @return String
     * @description 根据提供的授权码获取QQ的访问令牌（accessToken）
     */
    private String getQQAccessToken(String code) {
        /**
         * 返回结果是字符串 access_token=*&expires_in=7776000&refresh_token=* 返回错误 callback({UcWebConstants.VIEW_OBJ_RESULT_KEY:111,error_description:"error msg"})
         */
        String accessToken = null;
        String url = null;
        try {
            url = String.format(appConfig.getQqUrlAccessToken(), appConfig.getQqAppId(), appConfig.getQqAppKey(), code, URLEncoder.encode(appConfig
                    .getQqUrlRedirect(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("encode失败");
        }
        String tokenResult = OKHttpUtils.getRequest(url);
        if (tokenResult == null || tokenResult.indexOf(Constants.VIEW_OBJ_RESULT_KEY) != -1) {
            logger.error("获取qqToken失败:{}", tokenResult);
            throw new BusinessException("获取qqToken失败");
        }
        String[] params = tokenResult.split("&");
        if (params != null) {
            for (String p : params) {
                if (p.indexOf("access_token") != -1) {
                    accessToken = p.split("=")[1];
                    break;
                }
            }
        }
        return accessToken;
    }

    /**
     * @param accessToken
     * @return String
     * @description 根据访问令牌获取QQ的OpenID
     */
    private String getQQOpenId(String accessToken) throws BusinessException {
        // 获取openId
        String url = String.format(appConfig.getQqUrlOpenId(), accessToken);
        String openIDResult = OKHttpUtils.getRequest(url);
        String tmpJson = this.getQQResp(openIDResult);
        if (tmpJson == null) {
            logger.error("调qq接口获取openID失败:tmpJson{}", tmpJson);
            throw new BusinessException("调qq接口获取openID失败");
        }
        Map jsonData = JsonUtils.convertJson2Obj(tmpJson, Map.class);
        if (jsonData == null || jsonData.containsKey(Constants.VIEW_OBJ_RESULT_KEY)) {
            logger.error("调qq接口获取openID失败:{}", jsonData);
            throw new BusinessException("调qq接口获取openID失败");
        }
        return String.valueOf(jsonData.get("openid"));
    }


    /**
     * @param accessToken
     * @param qqOpenId
     * @return QQInfoDto
     * @description 根据访问令牌和QQ的OpenID获取QQ用户的信息
     */
    private QQInfoDto getQQUserInfo(String accessToken, String qqOpenId) throws BusinessException {
        String url = String.format(appConfig.getQqUrlUserInfo(), accessToken, appConfig.getQqAppId(), qqOpenId);
        String response = OKHttpUtils.getRequest(url);
        if (StringUtils.isNotBlank(response)) {
            QQInfoDto qqInfo = JsonUtils.convertJson2Obj(response, QQInfoDto.class);
            if (qqInfo.getRet() != 0) {
                logger.error("qqInfo:{}", response);
                throw new BusinessException("调qq接口获取用户信息异常");
            }
            return qqInfo;
        }
        throw new BusinessException("调qq接口获取用户信息异常");
    }

    /**
     * @param result
     * @return String
     * @description 从QQ接口的响应中提取出有效的JSON字符串
     */
    private String getQQResp(String result) {
        if (StringUtils.isNotBlank(result)) {
            int pos = result.indexOf("callback");
            if (pos != -1) {
                int start = result.indexOf("(");
                int end = result.lastIndexOf(")");
                String jsonStr = result.substring(start + 1, end - 1);
                return jsonStr;
            }
        }
        return null;
    }

    /**
     * @param userId
     * @param changeSpace
     * @return void
     * @description 更改用户的存储空间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserSpace(String userId, Integer changeSpace) {
        Long space = changeSpace * Constants.MB;
        this.userInfoMapper.updateUserSpace(userId, null, space);
        redisComponent.resetUserSpaceUse(userId);
    }
}

