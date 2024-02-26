package com.skybox;

import com.skybox.entity.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.spring.ApplicationContextProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox
 * @ClassName: SkyBoxApplication
 * @Datetime: 2023/11/11 16:11
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 启动Spring Boot应用，并配置一些相关的注解和Bean。其中使用了异步方法执行、数据库持久化、定时任务等功能。还配置了文件上传时的临时保存路径。
 */

@EnableAsync // 启用异步调用
@EnableScheduling // 启用定时任务
@EnableTransactionManagement // 启用事务管理
@MapperScan("com.skybox.mappers")   // 扫描指定包路径下的MyBatis Mapper接口，并将其注册为Spring Bean，以便进行数据库操作
@SpringBootApplication(scanBasePackages = {"com.skybox"})  // 一个Spring Boot应用程序的主类，并且启用了Spring Boot的自动配置

public class SkyBoxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyBoxApplication.class, args);
    }

    @Bean
    @DependsOn({"applicationContextProvider"})
    MultipartConfigElement multipartConfigElement() {
        AppConfig appConfig = (AppConfig) ApplicationContextProvider.getBean("appConfig");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP);
        return factory.createMultipartConfig();
    }
}

