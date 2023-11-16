package com.skybox.exception;

import com.skybox.entity.enums.ResponseCodeEnum;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.exception
 * @ClassName: BusinessException
 * @Datetime: 2023/11/15 19:56
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示业务操作中发生的异常情况
 */

public class BusinessException extends RuntimeException {
    // 响应代码枚举类型，用于标识异常的响应代码
    private ResponseCodeEnum codeEnum;
    // 异常的响应代码，通常与 codeEnum 中的代码相同
    private Integer code;
    // 异常的详细信息
    private String message;

    /**
     * @param message
     * @param e
     * @return null
     * @description 带有详细信息和原始异常对象的构造函数
     */
    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    /**
     * @param message
     * @return null
     * @description 仅带有详细信息的构造函数
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * @param e
     * @return null
     * @description 仅带有原始异常对象的构造函数
     */
    public BusinessException(Throwable e) {
        super(e);
    }

    /**
     * @param codeEnum
     * @return null
     * @description 根据响应代码枚举类型创建异常对象，异常的详细信息、响应代码和枚举类型中定义的信息相关联
     */
    public BusinessException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.codeEnum = codeEnum;
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    /**
     * @param code
     * @param message
     * @return null
     * @description 根据响应代码和详细信息创建异常对象
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * @param
     * @return ResponseCodeEnum
     * @description 获取异常的响应代码枚举类型
     */
    public ResponseCodeEnum getCodeEnum() {
        return codeEnum;
    }

    /**
     * @param
     * @return Integer
     * @description 获取异常的响应代码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @param
     * @return String
     * @description 获取异常的详细信息
     */
    @Override
    public String getMessage() {
        return message;
    }

    // 重写了父类的 fillInStackTrace() 方法，不返回堆栈信息，提高效率
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}

