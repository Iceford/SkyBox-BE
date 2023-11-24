package com.skybox.controller;

import com.skybox.controller.basecontroller.BaseController;
import com.skybox.entity.enums.ResponseCodeEnum;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: AGlobalExceptionHandlerController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于捕获和处理应用程序中出现的异常
 */

@RestControllerAdvice
public class AGlobalExceptionHandlerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    /**
     * @param e       异常对象
     * @param request HttpServletRequest对象
     * @return Object
     * @description 处理Exception类型的异常
     */
    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request) {
        // 使用日志记录器记录异常信息和请求地址
        logger.error("请求错误，请求地址{},错误信息:", request.getRequestURL(), e);
        // 根据不同的异常类型，设置不同的响应码、响应消息和状态
        ResponseVO ajaxResponse = new ResponseVO();
        if (e instanceof NoHandlerFoundException) {
            // 请求的处理器不存在，设置响应码为404
            ajaxResponse.setCode(ResponseCodeEnum.CODE_404.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_404.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof BusinessException) {
            // 业务错误，根据具体的业务异常设置响应码和响应消息
            BusinessException biz = (BusinessException) e;
            ajaxResponse.setCode(biz.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : biz.getCode());
            ajaxResponse.setInfo(biz.getMessage());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof BindException || e instanceof MethodArgumentTypeMismatchException) {
            // 参数类型错误，设置响应码为600
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_600.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else if (e instanceof DuplicateKeyException) {
            // 主键冲突，设置响应码为601
            ajaxResponse.setCode(ResponseCodeEnum.CODE_601.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_601.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        } else {
            // 其他类型的异常，设置响应码为500
            ajaxResponse.setCode(ResponseCodeEnum.CODE_500.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_500.getMsg());
            ajaxResponse.setStatus(STATUC_ERROR);
        }
        // 返回封装了响应码、响应消息和状态的ResponseVO对象
        return ajaxResponse;
    }
}

