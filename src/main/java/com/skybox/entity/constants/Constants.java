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
    // 整型常量

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Integer LENGTH_30 = 30;

    public static final Integer LENGTH_10 = 10;

    public static final Integer LENGTH_20 = 20;

    public static final Integer LENGTH_5 = 5;

    public static final Integer LENGTH_15 = 15;

    public static final Integer LENGTH_150 = 150;

    public static final Integer LENGTH_50 = 50;

    // 字符串常量

    public static final String ZERO_STR = "0";
    public static final String SESSION_KEY = "session_key";

    public static final String SESSION_SHARE_KEY = "session_share_key_";

    public static final String FILE_FOLDER_FILE = "/file/";

    public static final String FILE_FOLDER_TEMP = "/temp/";

    public static final String IMAGE_PNG_SUFFIX = ".png";

    public static final String TS_NAME = "index.ts";

    public static final String M3U8_NAME = "index.m3u8";

    public static final String CHECK_CODE_KEY = "check_code_key";

    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    public static final String AVATAR_SUFFIX = ".jpg";

    public static final String FILE_FOLDER_AVATAR_NAME = "avatar/";

    public static final String AVATAR_DEFUALT = "default_avatar.jpg";

    public static final String VIEW_OBJ_RESULT_KEY = "result";

    // redis key 相关

    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;

    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;

    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;

    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;

    public static final Long MB = 1024 * 1024L;


    // Redis键常量
    public static final String REDIS_KEY_DOWNLOAD = "skybox:download:";

    public static final String REDIS_KEY_SYS_SETTING = "skybox:syssetting:";

    public static final String REDIS_KEY_USER_SPACE_USE = "skybox:user:spaceuse:";

    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "skybox:user:file:temp:";

}

