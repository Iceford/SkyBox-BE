package com.skybox.utils;

import com.skybox.entity.constants.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: StringTools
 * @Datetime: 2023/11/12 21:28
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一些字符串处理的方法
 */

public class StringTools {
    /**
     * @param originString
     * @return String
     * @description 对字符串进行MD5加密
     */
    public static String encodeByMD5(String originString) {
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    /**
     * @param str
     * @return boolean
     * @description 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * @param fileName
     * @return String
     * @description 获取文件名的后缀名
     */
    public static String getFileSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String suffix = fileName.substring(index);
        return suffix;
    }

    /**
     * @param fileName
     * @return String
     * @description 获取去除后缀的文件名
     */
    public static String getFileNameNoSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    /**
     * @param fileName
     * @return String
     * @description 重命名文件
     */
    public static String rename(String fileName) {
        // 获取不加后缀的文件名
        String fileNameReal = getFileNameNoSuffix(fileName);
        // 获取文件的后缀名
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Constants.LENGTH_5) + suffix;
    }

    /**
     * @param count
     * @return String
     * @description 生成指定长度的随机字符串，包含字母和数字
     */
    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    /**
     * @param count
     * @return String
     * @description 生成指定长度的随机数字字符串
     */
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    /**
     * @param content
     * @return String
     * @description 对标题进行转义处理
     */
    public static String escapeTitle(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;");
        return content;
    }

    /**
     * @param content
     * @return String
     * @description 对HTML内容进行转义处理
     */
    public static String escapeHtml(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;");
        content = content.replace(" ", "&nbsp;");
        content = content.replace("\n", "<br>");
        return content;
    }

    /**
     * @param path
     * @return boolean
     * @description 检查路径是否合法
     */
    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        return true;
    }
}

