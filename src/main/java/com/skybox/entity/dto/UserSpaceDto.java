package com.skybox.entity.dto;

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

public class UserSpaceDto implements Serializable {
    // 用户已使用的存储空间大小
    private Long useSpace;
    // 用户总的存储空间大小
    private Long totalSpace;

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }
}

