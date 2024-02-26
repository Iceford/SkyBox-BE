package com.skybox.service;

import com.skybox.entity.po.EmailCode;
import com.skybox.entity.query.EmailCodeQuery;
import com.skybox.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: EmailCodeService
 * @Datetime: 2023/11/11 20:53
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于定义邮箱验证码的业务逻辑方法
 */

public interface EmailCodeService {

    /**
     * 根据条件查询列表
     */
    List<EmailCode> findListByParam(EmailCodeQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(EmailCodeQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param);

    /**
     * 新增
     */
    Integer add(EmailCode bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<EmailCode> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<EmailCode> listBean);

    /**
     * 根据EmailAndCode查询对象
     */
    EmailCode getEmailCodeByEmailAndCode(String email, String code);


    /**
     * 根据EmailAndCode修改
     */
    Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code);


    /**
     * 根据EmailAndCode删除
     */
    Integer deleteEmailCodeByEmailAndCode(String email, String code);

    /**
     * 发送邮箱验证码
     */
    void sendEmailCode(String toEmail, Integer type);

    /**
     * 检查验证码
     */
    void checkCode(String email, String code);
}
