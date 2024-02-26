package com.skybox.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@JsonIgnoreProperties(ignoreUnknown = true) // 用于在序列化和反序列化 JSON 数据时忽略未知的属性
public class SysSettingsDto implements Serializable {

    // 注册发送邮件标题
    private String registerEmailTitle = "SkyBox邮箱验证码";

    // 注册发送邮件内容
    private String registerEmailContent = "你好!" + "\n\n" + "我们已收到你要求获得 SkyBox 帐户所用的一次性邮箱验证码的申请。" + "\n\n" + "您的邮箱验证码是：%s，15分钟内有效" + "如果你没有请求验证码，可放心忽略这封电子邮件。别人可能错误地键入了你的电子邮件地址。" + "\n\n" + "谢谢!";

    // 用户初始化空间大小（单位：MB）
    private Integer userInitUseSpace = 1024;

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(Integer userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }

}

