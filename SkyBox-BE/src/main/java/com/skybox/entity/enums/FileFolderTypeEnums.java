package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: FileFolderTypeEnums
 * @Datetime: 2023/11/11 18:19
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件或目录的类型
 */

public enum FileFolderTypeEnums {
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private Integer type;
    private String desc;

    FileFolderTypeEnums(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * @param
     * @return Integer
     * @description 获取枚举常量的类型
     */
    public Integer getType() {
        return type;
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

