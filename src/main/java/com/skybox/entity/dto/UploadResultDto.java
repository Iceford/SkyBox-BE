package com.skybox.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.dto
 * @ClassName: UploadResultDto
 * @Datetime: 2023/11/11 19:25
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输上传结果相关的信息
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultDto implements Serializable {
    // 上传文件的唯一标识符
    private String fileId;
    // 上传结果的状态
    private String status;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

