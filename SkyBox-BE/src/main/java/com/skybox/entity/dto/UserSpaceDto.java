package com.skybox.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: UserSpaceDto
 * @Datetime: 2023/11/11 19:25
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输用户存储空间相关的信息
 */

@Data
public class UserSpaceDto implements Serializable {
    // 用户已使用的存储空间大小
    private Long useSpace;
    // 用户总的存储空间大小
    private Long totalSpace;

}

