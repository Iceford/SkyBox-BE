package com.skybox.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.spring
 * @ClassName: ApplicationContextProvider
 * @Datetime: 2023/12/15 4:22
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 提供了一种获取和管理ApplicationContext对象的工具类。通过静态方法getBean，可以方便地获取Spring容器中的Bean对象，可以通过名称或类型来获取。这在需要在非Spring管理的类中获取Spring Bean对象时非常有用
 */

@Component("applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextProvider.class);
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("获取bean异常", e);
            return null;
        }

    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}

