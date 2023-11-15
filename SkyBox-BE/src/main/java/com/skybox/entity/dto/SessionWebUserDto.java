package com.skybox.entity.dto;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: SessionWebUserDto
 * @Datetime: 2023/11/11 19:23
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输会话中的用户信息
 */

import lombok.Data;

@Data
public class SessionWebUserDto {
    // 会话中的用户昵称
    private String nickName;
    // 会话中的用户ID，即当前会话用户的唯一标识符
    private String userId;
    // 会话中的用户是否是管理员
    private Boolean isAdmin;
    // 会话中的用户已使用的存储空间大小
    private Long useSpace;
    // 会话中的用户总的存储空间大小
    private Long totalSpace;
    // 会话中的用户头像的路径或URL
    private String avatar;
}

