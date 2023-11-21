package com.skybox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox
 * @ClassName: SkyBoxApplication
 * @Datetime: 2023/11/11 16:11
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个Java应用程序的入口类，用于启动Spring Boot应用程序
 */

@SpringBootApplication  // 一个Spring Boot应用程序的主类，并且启用了Spring Boot的自动配置
@EnableTransactionManagement // 启用事务管理
@EnableScheduling // 启用定时任务
@EnableAsync // 启用异步调用
@MapperScan("com.skybox.mappers")   // 扫描指定包路径下的MyBatis Mapper接口，并将其注册为Spring Bean，以便进行数据库操作
public class SkyBoxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyBoxApplication.class, args);
    }
}

