package com.skybox.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: ScaleFilter
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一些图片和视频处理的方法
 */

@Slf4j
public class ScaleFilter {
    /**
     * @param file
     * @param thumbnailWidth
     * @param targetFile
     * @param delSource
     * @return Boolean
     * @description 使用FFmpeg库创建指定宽度的缩略图
     */
    public static Boolean createThumbnailWidthFFmpeg(File file, int thumbnailWidth, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(file);
            //thumbnailWidth 缩略图的宽度   thumbnailHeight 缩略图的高度
            int sorceW = src.getWidth();
            int sorceH = src.getHeight();
            //小于 指定高宽不压缩
            if (sorceW <= thumbnailWidth) {
                return false;
            }
            compressImage(file, thumbnailWidth, targetFile, delSource);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param sourceFile
     * @param widthPercentage
     * @param targetFile
     * @return void
     * @description 根据给定的宽度百分比压缩图像
     */
    public static void compressImageWidthPercentage(File sourceFile, BigDecimal widthPercentage, File targetFile) {
        try {
            BigDecimal widthResult = widthPercentage.multiply(new BigDecimal(ImageIO.read(sourceFile).getWidth()));
            compressImage(sourceFile, widthResult.intValue(), targetFile, true);
        } catch (Exception e) {
            log.error("压缩图片失败");
        }
    }

    /**
     * @param sourceFile
     * @param width
     * @param targetFile
     * @return void
     * @description 生成视频的封面图像
     */
    public static void createCover4Video(File sourceFile, Integer width, File targetFile) {
        try {
            String cmd = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsoluteFile(), width, width, targetFile.getAbsoluteFile()), false);
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
        }
    }

    /**
     * @param sourceFile
     * @param width
     * @param targetFile
     * @param delSource
     * @return void
     * @description 压缩图像到指定的宽度
     */
    public static void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        try {
            String cmd = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsoluteFile(), width, targetFile.getAbsoluteFile()), false);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        } catch (Exception e) {
            log.error("压缩图片失败");
        }
    }

    /**
     * @param args
     * @return void
     * @description 示例方法
     */
    public static void main(String[] args) {
        compressImageWidthPercentage(new File("C:\\Users\\Administrator\\Pictures\\微信图片_20230107141436.png"), new BigDecimal(0.7),
                new File("C:\\Users\\Administrator" +
                        "\\Pictures" +
                        "\\微信图片_202106281029182.jpg"));
    }
}
