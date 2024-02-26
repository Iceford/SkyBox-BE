package com.skybox.controller.commonfilecontroller;

import com.skybox.component.RedisComponent;
import com.skybox.controller.basecontroller.BaseController;
import com.skybox.entity.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.DownloadFileDto;
import com.skybox.entity.enums.FileCategoryEnums;
import com.skybox.entity.enums.FileFolderTypeEnums;
import com.skybox.entity.enums.ResponseCodeEnum;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.FolderVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.exception.BusinessException;
import com.skybox.service.FileInfoService;
import com.skybox.utils.CopyTools;
import com.skybox.utils.StringTools;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller.commonfilecontroller
 * @ClassName: CommonFileController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 提供一些通用的文件处理功能
 */

public class CommonFileController extends BaseController {

    @Resource
    protected FileInfoService fileInfoService;

    @Resource
    protected AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    /**
     * @param path
     * @param userId
     * @return ResponseVO
     * @description 根据给定的路径和用户ID，查询文件夹信息，并返回包含文件夹信息的ResponseVO对象。该方法根据路径中的文件ID进行排序，并根据排序结果进行查询，返回文件夹信息列表。
     */
    protected ResponseVO getFolderInfo(String path, String userId) {
        String[] pathArray = path.split("/");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        infoQuery.setFileIdArray(pathArray);
        String orderBy = "field(file_id,\"" + StringUtils.join(pathArray, "\",\"") + "\")";
        infoQuery.setOrderBy(orderBy);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FolderVO.class));
    }

    /**
     * @param response
     * @param imageFolder
     * @param imageName
     * @return void
     * @description 根据给定的图片文件夹和图片名称，从指定路径读取图片文件，并将其输出到HttpServletResponse中，用于获取缩略图
     */
    public void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + "/" + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }


    /**
     * @param response
     * @param fileId
     * @param userId
     * @return void
     * @description 根据给定的文件ID和用户ID，从指定路径读取文件，并将其输出到HttpServletResponse中。如果文件是.ts文件，则获取分片文件；否则，获取.m3u8索引文件或其他文件。该方法根据文件的类型确定文件路径，并进行文件读取操作
     */
    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if (fileId.endsWith(".ts")) {
            String[] tsAarray = fileId.split("_");
            String realFileId = tsAarray[0];
            //根据原文件的id查询出一个文件集合
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                //分享的视频，ts路径记录的是原视频的id,这里通过id直接取出原视频
                FileInfoQuery fileInfoQuery = new FileInfoQuery();
                fileInfoQuery.setFileId(realFileId);
                List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
                fileInfo = fileInfoList.get(0);
                if (fileInfo == null) {
                    return;
                }

                //更具当前用户id和路径去查询当前用户是否有该文件，如果没有直接返回
                fileInfoQuery = new FileInfoQuery();
                fileInfoQuery.setFilePath(fileInfo.getFilePath());
                fileInfoQuery.setUserId(userId);
                Integer count = fileInfoService.findCountByParam(fileInfoQuery);
                if (count == 0) {
                    return;
                }
            }
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;
        } else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            //视频文件读取.m3u8文件
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                //重新设置文件路径
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            } else {
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        readFile(response, filePath);
    }


    /**
     * @param fileId
     * @param userId
     * @return ResponseVO
     * @description 根据给定的文件ID和用户ID，创建一个下载链接，并返回包含下载链接的ResponseVO对象。该方法生成一个随机的下载代码，并将下载链接保存到Redis数据库中。
     */
    protected ResponseVO createDownloadUrl(String fileId, String userId) {
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 不能下载文件夹
        if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // fileId正常，得到50位随机code
        String code = StringTools.getRandomString(Constants.LENGTH_50);
        DownloadFileDto downloadFileDto = new DownloadFileDto();
        downloadFileDto.setDownloadCode(code);
        downloadFileDto.setFilePath(fileInfo.getFilePath());
        downloadFileDto.setFileName(fileInfo.getFileName());
        redisComponent.saveDownloadCode(code, downloadFileDto);
        return getSuccessResponseVO(code);
    }

    /**
     * @param request
     * @param response
     * @param code
     * @return void
     * @description 根据给定的下载代码，从Redis数据库中获取下载链接信息，并将文件下载到客户端。该方法设置了响应的内容类型和头部信息，以便浏览器能够正确处理下载文件。
     */
    protected void download(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
        DownloadFileDto downloadFileDto = redisComponent.getDownloadCode(code);
        if (downloadFileDto == null) {
            return;
        }
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + "/" + downloadFileDto.getFilePath();
        String fileName = downloadFileDto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {// IE浏览器
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        readFile(response, filePath);
    }
}

