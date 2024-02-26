package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.component.RedisComponent;
import com.skybox.controller.basecontroller.BaseController;
import com.skybox.entity.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.CreateImageCode;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.enums.VerifyRegexEnum;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.exception.BusinessException;
import com.skybox.service.EmailCodeService;
import com.skybox.service.UserInfoService;
import com.skybox.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: AccountController
 * @Datetime: 2023/11/12 20:58
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个Spring MVC控制器类，用于处理与用户账户相关的请求
 */

@RestController("accountController")
public class AccountController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
    @Resource
    private AppConfig appConfig;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private EmailCodeService emailCodeService;
    @Resource
    private RedisComponent redisComponent;

    /**
     * @param response
     * @param session
     * @param type
     * @return void
     * @description 生成和返回验证码，并将其存储在会话中
     */
    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session
            , @RequestParam(value = "type", required = false) Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache"); // 响应消息不能缓存
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        String code = vCode.getCode();
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }

    /**
     * @param session
     * @param email
     * @param checkCode
     * @param type
     * @return ResponseVO
     * @description 发送邮箱验证码
     */
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type) {
        try {
            // 如果图片验证码错误
            if (!checkCode.equals(session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException("图片验证码不正确");
            }
            // 如果图片验证码正确，发送邮箱验证码
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            // 删除session中保存的邮箱验证码
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }

    /**
     * @param session
     * @param email
     * @param nickName
     * @param password
     * @param checkCode
     * @param emailCode
     * @return ResponseVO
     * @description 处理用户注册请求
     */
    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, max = 20) String nickName,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @param session
     * @param email
     * @param password
     * @param checkCode
     * @return ResponseVO
     * @description 处理用户登录请求
     */
    @RequestMapping("/login")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO login(HttpSession session,
                            @VerifyParam(required = true) String email,
                            @VerifyParam(required = true) String password,
                            @VerifyParam(required = true) String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
            return getSuccessResponseVO(sessionWebUserDto);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @param session
     * @param email
     * @param password
     * @param checkCode
     * @param emailCode
     * @return ResponseVO
     * @description 处理用户重置密码请求
     */
    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                               @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
                               @VerifyParam(required = true) String checkCode,
                               @VerifyParam(required = true) String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    /**
     * @param response
     * @param userId
     * @return void
     * @description 获取用户头像
     */
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void getAvatar(HttpServletResponse response,
                          @VerifyParam(required = true) @PathVariable("userId") String userId) {
        // 得到头像根目录 = /file + /avatar
        String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
        // 从AppConfig中得到项目根目录，从而得到放置头像文件夹的绝对路径
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            // 不存在就创建
            folder.mkdirs();
        }
        // 根据userId得到用户头像的绝对路径 = 放置头像文件夹的绝对路径 + userId + .jpg(统一后缀)
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + "/" + userId + Constants.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        // 如果找不到该用户的头像
        if (!file.exists()) {
            // 默认头
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + "/" + Constants.AVATAR_DEFUALT;
            // 获取系统默认头像
            if (!new File(avatarPath).exists()) {
                // 获取默认头像失败
                printNoDefaultImage(response);
                return;
            }
        }
        // 输出
        response.setContentType("image/jpg");
        readFile(response, avatarPath);
    }

    /**
     * @param response
     * @return void
     * @description 解决输出默认头像失败问题
     */
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放置默认头像default_avatar.jpg");
            writer.close();
        } catch (Exception e) {
            logger.error("输出无默认图失败", e);
        } finally {
            writer.close();
        }
    }

    /**
     * @param session
     * @return ResponseVO
     * @description 获取用户信息
     */
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor
    public ResponseVO getUserInfo(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVO(sessionWebUserDto);
    }

    /**
     * @param session
     * @return ResponseVO
     * @description 获取用户的空间使用情况
     */
    @PostMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        return getSuccessResponseVO(redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId()));
    }

    /**
     * @param session
     * @return ResponseVO
     * @description 处理用户注销请求
     */
    @PostMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param avatar
     * @return ResponseVO
     * @description 更新用户头像
     */
    @RequestMapping("/updateUserAvatar")
    @GlobalInterceptor
    public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {

        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        // 得到头像文件夹
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
        // 如果不存在就创建
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();
        }
        // 得到新头像绝对路径
        File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
        try {
            // 输出
            avatar.transferTo(targetFile);
        } catch (Exception e) {
            logger.error("上传头像失败", e);
        }

        // 同时将数据库中qq头像设为空
        UserInfo userInfo = new UserInfo();
        userInfo.setQqAvatar("");
        userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
        webUserDto.setAvatar(null);
        //更新session
        session.setAttribute(Constants.SESSION_KEY, webUserDto);
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param password
     * @return ResponseVO
     * @description 更新用户密码
     */
    @PostMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                     @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
        SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfoService.updateUserInfoByUserId(userInfo, sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param callbackUrl
     * @return ResponseVO
     * @description QQ登录授权操作
     */
    @RequestMapping("qqlogin")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO qqlogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
        // 生成一个长度为30的随机字符串state
        String state = StringTools.getRandomString(Constants.LENGTH_30);
        // 如果callbackUrl参数不为空，将该参数与state作为键值对存储在HttpSession对象中，以便在后续的操作中使用
        if (!StringTools.isEmpty(callbackUrl)) {
            session.setAttribute(state, callbackUrl);
        }
        // 使用String.format方法构建URL：该URL是根据配置文件中的qqUrlAuthorization、qqAppId、qqUrlRedirect和state生成的。其中，qqUrlAuthorization表示QQ的授权地址，qqAppId是用于标识应用的ID，qqUrlRedirect是授权成功后的回调URL，state是前面生成的随机字符串
        String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(), URLEncoder.encode(appConfig.getQqUrlRedirect(), "utf-8"), state);
        // 返回一个表示成功响应的ResponseVO对象
        return getSuccessResponseVO(url);
    }

    /**
     * @param session
     * @param code
     * @param state
     * @return ResponseVO
     * @description 处理 QQ 登录回调请求
     */
    @RequestMapping("qqlogin/callback")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO qqLoginCallback(HttpSession session,
                                      @VerifyParam(required = true) String code,
                                      @VerifyParam(required = true) String state) {
        // 调用 userInfoService 的 qqLogin 方法，将授权码 code 作为参数传递给该方法进行 QQ 登录操作
        SessionWebUserDto sessionWebUserDto = userInfoService.qqLogin(code);
        session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
        Map<String, Object> result = new HashMap<>();
        result.put("callbackUrl", session.getAttribute(state));
        result.put("userInfo", sessionWebUserDto);
        return getSuccessResponseVO(result);
    }

}

