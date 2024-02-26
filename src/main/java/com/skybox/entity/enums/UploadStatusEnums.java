package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: UploadStatusEnums
 * @Datetime: 2023/11/11 18:26
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件上传的状态
 */

public enum UploadStatusEnums {
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_FINISH("upload_finish", "上传完成");

    private final String code;
    private final String desc;

    UploadStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * @param
     * @return String
     * @description 获取状态代码
     */
    public String getCode() {
        return code;
    }

    /**
     * @param
     * @return String
     * @description 获取对应的描述
     */
    public String getDesc() {
        return desc;
    }
}

