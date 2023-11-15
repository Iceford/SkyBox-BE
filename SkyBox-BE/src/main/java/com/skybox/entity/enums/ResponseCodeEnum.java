package com.skybox.entity.enums;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.enums
 * @ClassName: ResponseCodeEnum
 * @Datetime: 2023/11/11 18:24
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示不同的响应代码和对应的消息
 */

public enum ResponseCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_500(500, "服务器返回错误，请联系管理员"),
    CODE_901(901, "登录超时，请重新登录"),
    CODE_902(902, "分享连接不存在，或者已失效"),
    CODE_903(903, "分享验证失效，请重新验证"),
    CODE_904(904, "网盘空间不足，请扩容");
    private Integer code;

    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * @param
     * @return Integer
     * @description 获取响应代码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param
     * @return String
     * @description 获取对应的消息
     */
    public String getMsg() {
        return msg;
    }
}

