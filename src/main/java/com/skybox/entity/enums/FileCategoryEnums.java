package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: FileCategoryEnums
 * @Datetime: 2023/11/11 18:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示文件的分类
 */

public enum FileCategoryEnums {
    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音频"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其他");

    private final Integer category;
    private final String code;
    private final String desc;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }

    /**
     * @param code
     * @return FileCategoryEnums
     * @description 根据给定的代码获取相应的枚举常量
     */
    public static FileCategoryEnums getByCode(String code) {
        // 遍历所有枚举常量，检查其代码是否与给定的代码匹配，如果匹配则返回相应的枚举常量
        for (FileCategoryEnums item : FileCategoryEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        // 如果找不到匹配的枚举常量，则返回null
        return null;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }
}

