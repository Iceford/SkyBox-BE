package com.skybox.config;

import com.skybox.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.config
 * @ClassName: AppConfig
 * @Datetime: 2023/11/11 20:36
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于读取和管理应用程序的配置信息
 */

@Component
public class AppConfig {
    // 用于记录日志的 Logger 对象
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    // 文件目录的配置属性，使用 @Value 注解从配置文件中读取，如果未指定则默认为空字符串
    @Value("${project.folder:}")
    private String projectFolder;
    // 发送人的配置属性，使用 @Value 注解从配置文件中读取，如果未指定则默认为空字符串
    @Value("${spring.mail.username:}")
    private String sendUserName;
    // 管理员电子邮件的配置属性，使用 @Value 注解从配置文件中读取，如果未指定则默认为空字符串
    @Value("${admin.emails:}")
    private String adminEmails;
    // 是否为开发环境的配置属性，使用 @Value 注解从配置文件中读取，默认为 false
    @Value("${dev:false}")
    private Boolean dev;
    /**
     * QQ 登录相关的配置属性，使用 @Value 注解从配置文件中读取
     */
    @Value("${qq.app.id:}")
    private String qqAppId;
    @Value("${qq.app.key:}")
    private String qqAppKey;
    @Value("${qq.url.authorization:}")
    private String qqUrlAuthorization;
    @Value("${qq.url.access.token:}")
    private String qqUrlAccessToken;
    @Value("${qq.url.openid:}")
    private String qqUrlOpenId;
    @Value("${qq.url.user.info:}")
    private String qqUrlUserInfo;
    @Value("${qq.url.redirect:}")
    private String qqUrlRedirect;

    // 获取记录日志的 Logger 对象
    public static Logger getLogger() {
        return logger;
    }

    public String getAdminEmails() {
        return adminEmails;
    }

    // 获取文件目录配置属性的值，并在末尾添加斜杠（/）
    public String getProjectFolder() {
        if (!StringTools.isEmpty(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }

    // 获取发送人配置属性的值
    public String getSendUserName() {
        return sendUserName;
    }

    // 获取是否为开发环境的配置属性的值
    public Boolean getDev() {
        return dev;
    }

    /**
     * 获取相应的 QQ 登录配置属性的值
     */
    public String getQqAppId() {
        return qqAppId;
    }

    public String getQqAppKey() {
        return qqAppKey;
    }

    public String getQqUrlAuthorization() {
        return qqUrlAuthorization;
    }

    public String getQqUrlAccessToken() {
        return qqUrlAccessToken;
    }

    public String getQqUrlOpenId() {
        return qqUrlOpenId;
    }

    public String getQqUrlUserInfo() {
        return qqUrlUserInfo;
    }

    public String getQqUrlRedirect() {
        return qqUrlRedirect;
    }
}

