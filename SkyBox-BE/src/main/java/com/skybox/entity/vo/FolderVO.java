package com.skybox.entity.vo;

import lombok.Data;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.vo
 * @ClassName: FolderVO
 * @Datetime: 2023/11/11 19:34
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输文件夹相关的数据
 */

@Data
public class FolderVO {
    // 文件夹的名称
    private String fileName;
    // 文件夹的唯一标识符
    private String fileId;

}

