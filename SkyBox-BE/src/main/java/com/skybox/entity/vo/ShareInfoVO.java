package com.skybox.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.vo
 * @ClassName: ShareInfoVO
 * @Datetime: 2023/11/11 19:35
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示分享信息
 */

@Data
public class ShareInfoVO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    // 分享时间的日期对象
    private Date shareTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    // 分享到期时间的日期对象
    private Date expireTime;
    // 分享者的昵称
    private String nickName;
    // 被分享的文件名称
    private String fileName;
    // 当前用户是否是分享者本人
    private Boolean currentUser;
    // 被分享的文件的唯一标识符
    private String fileId;
    // 分享者的头像
    private String avatar;
    // 分享者的用户ID
    private String userId;
}

