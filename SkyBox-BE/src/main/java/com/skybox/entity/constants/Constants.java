package com.skybox.entity.constants;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.constants
 * @ClassName: Constants
 * @Datetime: 2023/11/11 16:53
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 常量类
 */

public class Constants {
    // 会话共享的键名
    public static final String SESSION_SHARE_KEY = "session_share_key_";
    // 验证码的键名
    public static final String CHECK_CODE_KEY = "check_code_key";
    // 邮箱验证码的键名
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";
    // 注册的初始值
    public static final int REGISTER_ZERO = 0;
    // 长度为5的值
    public static final Integer LENGTH_5 = 5;
    // 存储在Redis中系统设置的键名
    public static final String REDIS_KEY_SYS_SETTING = "skybox:syssetting";
    // 邮箱验证码过期时间
    public static final Integer LENGTH_15 = 15;
    // id长度
    public static final Integer LENGTH_10 = 10;
    // 1MB大小
    public static final Long MB = 1024 * 1024L;

    
    // Redis键的过期时间为1分钟
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;
    // Redis键的过期时间为1天
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    // Redis键的过期时间为1小时
    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;
    // Redis键的过期时间为5分钟
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;
    // Redis中下载相关的键名
    public static final String REDIS_KEY_DOWNLOAD = "skybox:download:";
    // Redis中用户空间使用情况的键名
    public static final String REDIS_KEY_USER_SPACE_USE = "skybox:user:spaceuse:";
    // Redis中用户临时文件大小的键名
    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "skybox:user:file:temp";


    // 会话的键名
    public static final String SESSION_KEY = "session_key";
    // 头像文件的后缀名
    public static final String AVATAR_SUFFIX = ".jpg";
    // 存储头像文件的文件夹名称
    public static final String FILE_FOLDER_AVATAR_NAME = "/avatar";
    // 默认头像的文件名
    public static final String AVATAR_DEFUALT = "default_avatar.jpg";
    // 存储文件的文件夹名称
    public static final String FILE_FOLDER_FILE = "/file";
    // 存储临时文件的文件夹名称
    public static final String FILE_FOLDER_TEMP = "/temp";
    // 排序规则
    public static final String ORDER_RULE = "folder_type desc, last_update_time desc";
    // PNG图片文件的后缀名
    public static final String IMAGE_PNG_SUFFIX = ".png";
    // 长度为150的值
    public static final Integer LENGTH_150 = 150;
    // TS文件的名称
    public static final String TS_NAME = "index.ts";
    // M3U8文件的名称
    public static final String M3U8_NAME = "index.m3u8";
    // 字符串"0"
    public static final String ZERO_STR = "0";
    // 长度为50的值
    public static final Integer LENGTH_50 = 50;
    // 长度为20的值
    public static final Integer LENGTH_20 = 20;
}

