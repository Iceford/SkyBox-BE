package com.skybox.utils;

import com.skybox.entity.enums.DateTimePatternEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: DateUtil
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于处理日期和时间相关的操作
 */

public class DateUtil {
    // 对象锁，用于线程同步
    private static final Object lockObj = new Object();
    // 使用线程局部变量 ThreadLocal 来存储不同日期格式对应的 SimpleDateFormat 对象，以保证线程安全性
    private static final Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * @param pattern
     * @return SimpleDateFormat
     * @description 根据给定的日期格式 pattern 获取对应的 SimpleDateFormat 对象
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * @param date
     * @param pattern
     * @return String
     * @description 将给定的日期 date 格式化为指定的日期格式 pattern，使用 getSdf(pattern) 方法获取对应的 SimpleDateFormat 对象，并调用其 format() 方法进行格式化
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    /**
     * @param dateStr
     * @param pattern
     * @return Date
     * @description 将给定的日期字符串 dateStr 解析为 Date 对象，使用 getSdf(pattern) 方法获取对应的 SimpleDateFormat 对象，并调用其 parse() 方法进行解析。如果解析失败，则打印异常信息，并返回当前时间的 Date 对象
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * @param day
     * @return Date
     * @description 获取当前日期之后的 day 天的日期。通过获取当前的 Calendar 实例，使用 add() 方法增加指定天数，然后返回新的日期对象
     */
    public static Date getAfterDate(Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    /**
     * @param args
     * @return void
     * @description 一个示例方法，用于测试 getAfterDate() 方法。它打印当前日期之后一天的日期，使用了 DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS 枚举来指定日期格式
     */
    public static void main(String[] args) {
        System.out.println(format(getAfterDate(1), DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}

