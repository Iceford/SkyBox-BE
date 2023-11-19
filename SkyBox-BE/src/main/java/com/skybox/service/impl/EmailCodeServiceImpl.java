package com.skybox.service.impl;

import com.skybox.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SysSettingsDto;
import com.skybox.entity.po.EmailCode;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.EmailCodeQuery;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.exception.BusinessException;
import com.skybox.mappers.EmailCodeMapper;
import com.skybox.mappers.UserInfoMapper;
import com.skybox.service.EmailCodeService;
import com.skybox.utils.RedisComponent;
import com.skybox.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: EmailCodeServiceImpl
 * @Datetime: 2023/11/11 21:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于处理邮箱验证码相关的业务逻辑
 */

@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    // 用于发送邮件的 JavaMailSender 对象
    @Resource
    private JavaMailSender javaMailSender;

    // 用于操作邮箱验证码的数据库映射器对象
    @Resource
    private EmailCodeMapper<EmailCode, EmailCodeQuery> emailCodeMapper;

    // 用于操作用户信息的数据库映射器对象
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    // 用于获取系统配置的对象
    @Resource
    private AppConfig appConfig;

    // 用于操作 Redis 缓存的组件
    @Resource
    private RedisComponent redisComponent;

    /**
     * @param toEmail 收件人邮箱地址
     * @param code    验证码
     * @return void
     * @description 发送邮件验证码的方法
     */
    private void sendEmailCode(String toEmail, String code) {
        try {
            // 使用 JavaMailSender 发送邮件
            MimeMessage message = javaMailSender.createMimeMessage();
            // 邮件的内容和主题根据系统配置进行设置
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // 邮件发件人
            helper.setFrom(appConfig.getSendUserName());
            // 邮件收件人 1或多个
            helper.setTo(toEmail);
            SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();

            // 邮件主题
            helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
            // 邮件内容
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
            // 邮件发送时间
            helper.setSentDate(new Date());
            // 发送
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error("邮件发送失败", e);
            throw new BusinessException("邮件发送失败");
        }
    }

    /**
     * @param toEmail 收件人邮箱地址
     * @param type    类型（0:注册  1:找回密码）
     * @return void
     * @description 发送邮箱验证码的前置和后置工作的方法
     */
    @Override
    @Transactional
    public void sendEmailCode(String toEmail, Integer type) {
        // 在发送验证码之前，根据类型进行相应的校验
        if (type == Constants.REGISTER_ZERO) {
            // 注册时检查邮箱是否已存在
            UserInfo userInfo = userInfoMapper.selectByEmail(toEmail);
            if (null != userInfo) {
                throw new BusinessException("邮箱已经存在");
            }
        }
        // 生成随机验证码
        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        // 发送邮件
        sendEmailCode(toEmail, code);
        // 在发送之前，还会将数据库中其他邮箱验证码置为不可用
        emailCodeMapper.disableEmailCode(toEmail);
        // 封装EmailCode对象
        EmailCode emailCode = new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(toEmail);
        emailCode.setStatus(Constants.REGISTER_ZERO);
        emailCode.setCreateTime(new Date());
        // 将新生成的验证码插入到数据库中
        emailCodeMapper.insert(emailCode);
    }

    /**
     * @param email 邮箱地址
     * @param code  验证码
     * @return void
     * @description 验证邮箱验证码的方法
     */
    @Override
    public void checkCode(String email, String code) {
        // 查询数据库中指定邮箱和验证码的记录，并进行相应的验证
        EmailCode emailCode = emailCodeMapper.selectByEmailAndCode(email, code);
        // 如果没查到数据，抛出业务异常
        if (emailCode == null) {
            throw new BusinessException("邮箱验证码不正确");
        }
        // 如果已经失效或超时，抛出业务异常
        if (emailCode.getStatus() == 1 || System.currentTimeMillis() - emailCode.getCreateTime().getTime() > Constants.LENGTH_15 * 1000 * 60) {
            throw new BusinessException("邮箱验证已失效");
        }
        emailCodeMapper.disableEmailCode(email);
    }

}

