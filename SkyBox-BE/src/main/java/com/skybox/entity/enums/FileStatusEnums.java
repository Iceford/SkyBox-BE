package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: FileStatusEnums
 * @Datetime: 2023/11/11 18:19
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件的状态
 */

public enum FileStatusEnums {
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");

    private Integer status;
    private String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * @param
     * @return Integer
     * @description 获取枚举常量的状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param
     * @return String
     * @description 获取枚举常量的描述
     */
    public String getDesc() {
        return desc;
    }
}

