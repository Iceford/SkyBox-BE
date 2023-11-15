package com.skybox.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: SessionShareDto
 * @Datetime: 2023/11/11 18:33
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输会话分享相关的信息
 */

@Data
public class SessionShareDto {
    // 会话分享的ID，用于唯一标识一个会话分享
    private String shareId;
    // 分享用户的ID，表示发起分享的用户ID
    private String shareUserId;
    // 会话分享的过期时间，表示会话分享的有效期截止时间
    private Date expireTime;
    // 文件的ID，表示被分享的文件的ID
    private String fileId;
}

