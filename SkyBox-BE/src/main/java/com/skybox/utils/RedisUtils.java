package com.skybox.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: RedisUtils
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 封装对Redis缓存的常用操作
 */

@Component
public class RedisUtils<V> {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    @Resource
    private RedisTemplate<String, V> redisTemplate;

    /**
     * @param key
     * @return void
     * @description 根据传入的一个或多个键值删除缓存
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * @param key
     * @return V
     * @description 根据键获取对应的缓存值
     */
    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @description 将键值对存入Redis缓存
     */
    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{},value:{}失败", key, value);
            return false;
        }
    }

    /**
     * @param key
     * @param value
     * @param time
     * @return boolean
     * @description 将键值对存入Redis缓存，并设置过期时间
     */
    public boolean setex(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redisKey:{},value:{}失败", key, value);
            return false;
        }
    }
}

