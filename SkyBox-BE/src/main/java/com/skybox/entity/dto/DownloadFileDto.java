package com.skybox.entity.dto;

import lombok.Data;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: DownloadFileDto
 * @Datetime: 2023/11/11 18:33
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输文件下载相关的信息
 */

@Data
public class DownloadFileDto {
    // 文件下载的代码，用于标识文件下载的唯一标识符
    private String downloadCode;
    // 文件的名称，表示被下载的文件的名称
    private String fileName;
    // 文件的路径，表示被下载的文件在系统中的路径
    private String filePath;
}

