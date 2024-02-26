package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: UserStatusEnum
 * @Datetime: 2023/11/11 18:28
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示用户的状态
 */

public enum UserStatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");


    private final Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * @param status
     * @return UserStatusEnum
     * @description 根据状态值获取对应的用户状态枚举常量
     */
    public static UserStatusEnum getByStatus(Integer status) {
        // 遍历所有枚举常量，如果找到与指定状态值相匹配的枚举常量，则返回该枚举常量
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        // 如果没有找到，则返回null
        return null;
    }

    /**
     * @param
     * @return Integer
     * @description 获取状态值
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param
     * @return String
     * @description 获取描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc
     * @return void
     * @description 设置描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

