package com.skybox.entity.vo;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.vo
 * @ClassName: ResponseVO
 * @Datetime: 2023/11/11 19:35
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示响应结果
 */

public class ResponseVO<T> {
    // 响应的状态
    private String status;
    // 响应的状态码，通常用整数表示，用于标识不同的响应状态
    private Integer code;
    // 响应的附加信息，用于描述响应结果的详细信息
    private String info;
    // 响应的数据内容，类型为泛型参数 T
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

