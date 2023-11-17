package com.skybox.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: CopyTools
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个名为 CopyTools 的工具类，使用了 Spring 框架提供的 BeanUtils 工具类来实现集合元素和单个对象的属性映射
 */

public class CopyTools {
    /**
     * @param list  源对象列表
     * @param clazz 目标对象的类
     * @return List<T>
     * @description 将一个源对象列表的属性映射到目标对象列表中
     */
    public static <T, S> List<T> copyList(List<S> list, Class<T> clazz) {
        return list.stream().map(s -> {
            T t = null;
            try {
                t = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
    }

    /**
     * @param s     源对象
     * @param clazz 目标对象的类 clazz
     * @return T
     * @description 将一个源对象的属性映射到目标对象中
     */
    public static <T, S> T copy(S s, Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;
    }
}

