package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: FileDelFlagEnums
 * @Datetime: 2023/11/11 18:18
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件的删除标志
 */

public enum FileDelFlagEnums {
    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");

    private final Integer flag;
    private final String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    /**
     * @param
     * @return Integer
     * @description 获取枚举常量的删除标志
     */
    public Integer getFlag() {
        return flag;
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

