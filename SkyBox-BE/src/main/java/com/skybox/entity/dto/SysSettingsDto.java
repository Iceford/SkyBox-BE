package com.skybox.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: SysSettingsDto
 * @Datetime: 2023/11/11 19:24
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输系统设置相关的信息
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SysSettingsDto implements Serializable {

    // 注册发送邮件标题
    private String registerEmailTitle = "邮箱验证码";

    // 注册发送邮件内容
    private String registerEmailContent = "你好，您的邮箱验证码是：%s，15分钟有效";

    // 用户初始化空间大小 5M
    private Integer userInitUseSpace = 5;

}

