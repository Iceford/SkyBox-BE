package com.skybox;

import com.skybox.component.RedisComponent;
import com.skybox.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox
 * @ClassName: CheckForRunning
 * @Datetime: 2023/12/15 4:15
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 在Spring Boot应用启动时检查数据库和Redis配置是否正确，如果配置正确则输出成功信息，如果配置有问题则输出错误信息并抛出异常。
 */

@Component("CheckForRunning")
public class CheckForRunning implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(CheckForRunning.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public void run(ApplicationArguments args) {
        try {
            dataSource.getConnection();
            redisComponent.getSysSettingsDto();
            logger.error("服务启动成功，可以开始愉快地开发了");
        } catch (Exception e) {
            logger.error("数据库或者redis设置失败，请检查配置");
            throw new BusinessException("服务启动失败");
        }
    }
}



