package com.skybox.entity.vo;


/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.vo
 * @ClassName: FolderVO
 * @Datetime: 2023/11/11 19:34
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于在不同层之间传输文件夹相关的数据
 */

public class FolderVO {
    private String fileName;
    private String fileId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}

