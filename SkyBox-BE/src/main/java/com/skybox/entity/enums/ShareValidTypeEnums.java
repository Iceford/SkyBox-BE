package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: ShareValidTypeEnums
 * @Datetime: 2023/11/11 18:24
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示分享的有效期类型
 */

public enum ShareValidTypeEnums {
    DAY_1(0, 1, "1天"),
    DAY_7(1, 7, "7天"),
    DAY_30(2, 30, "30天"),
    FOREVER(3, -1, "永久有效");

    private Integer type;
    private Integer days;
    private String desc;

    ShareValidTypeEnums(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }

    public static ShareValidTypeEnums getByType(Integer type) {
        for (ShareValidTypeEnums typeEnums : ShareValidTypeEnums.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }

    /**
     * @param
     * @return Integer
     * @description 获取类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param
     * @return Integer
     * @description 获取天数
     */
    public Integer getDays() {
        return days;
    }

    /**
     * @param
     * @return String
     * @description 获取描述
     */
    public String getDesc() {
        return desc;
    }
}

