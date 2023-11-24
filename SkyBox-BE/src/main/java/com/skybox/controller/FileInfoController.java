package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.controller.commonfilecontroller.CommonFileController;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.UploadResultDto;
import com.skybox.entity.enums.FileCategoryEnums;
import com.skybox.entity.enums.FileDelFlagEnums;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.FileInfoVO;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.service.FileInfoService;
import com.skybox.utils.CopyTools;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: FileInfoController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个RESTful风格的控制器(Controller)，用于处理文件相关的HTTP请求
 */

@RestController
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * @param session
     * @param query
     * @param category
     * @return ResponseVO
     * @description 加载文件数据列表
     */
    @PostMapping("/loadDataList")
    @GlobalInterceptor
    public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
        FileCategoryEnums categoryEnum = FileCategoryEnums.getByCode(category);
        if (categoryEnum != null) {
            query.setFileCategory(categoryEnum.getCategory());
        }
        query.setUserId(getUserInfoFromSession(session).getUserId());
        // 排序规则：先展示文件夹，然后按照修改时间排序
        query.setOrderBy(Constants.ORDER_RULE);
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        // 调用service方法得到分页对象，其中List<FileInfo>
        PaginationResultVO result = fileInfoService.findListByPage(query);
        // 集合元素映射为FileInfoVO再返回
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * @param session
     * @param fileId
     * @param file
     * @param fileName
     * @param filePid
     * @param fileMd5
     * @param chunkIndex
     * @param chunks
     * @return ResponseVO
     * @description 上传文件
     */
    @PostMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO uploadFile(HttpSession session, String fileId, MultipartFile file, @VerifyParam(required = true) String fileName, @VerifyParam(required = true) String filePid, @VerifyParam(required = true) String fileMd5, @VerifyParam(required = true) Integer chunkIndex, @VerifyParam(required = true) Integer chunks) {

        // 从session中获取SessionWebUserDto对象
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);

        UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);

        return getSuccessResponseVO(resultDto);
    }

    /**
     * @param response
     * @param imageFolder
     * @param imageName
     * @return void
     * @description 获取缩略图
     */
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    /**
     * @param response
     * @param session
     * @param fileId
     * @return void
     * @description 获取视频信息
     */
    @GetMapping("/ts/getVideoInfo/{fileId}")
    public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    /**
     * @param response
     * @param session
     * @param fileId
     * @return void
     * @description 获取文件
     */
    @RequestMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    /**
     * @param session
     * @param filePid
     * @param fileName
     * @return ResponseVO
     * @description 创建新的文件夹
     */
    @PostMapping("/newFoloder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO newFolder(HttpSession session, @VerifyParam(required = true) String filePid, @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(fileInfo);
    }

    /**
     * @param session
     * @param path
     * @return ResponseVO
     * @description 获取文件夹信息
     */
    @PostMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session, @VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, getUserInfoFromSession(session).getUserId());
    }

    /**
     * @param session
     * @param fileId
     * @param fileName
     * @return ResponseVO
     * @description 重命名文件或文件夹
     */
    @PostMapping("/rename")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO rename(HttpSession session, @VerifyParam(required = true) String fileId, @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
    }

    /**
     * @param session
     * @param filePid
     * @param currentFileIds
     * @return ResponseVO
     * @description 加载除当前文件夹外的所有文件夹
     */
    @PostMapping("/loadAllFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadAllFolder(HttpSession session, @VerifyParam(required = true) String filePid, String currentFileIds) {

        List<FileInfo> fileInfoList = fileInfoService.loadAllFolder(getUserInfoFromSession(session).getUserId(), filePid, currentFileIds);

        return getSuccessResponseVO(CopyTools.copyList(fileInfoList, FileInfoVO.class));
    }

    /**
     * @param session
     * @param fileIds
     * @param filePid
     * @return ResponseVO
     * @description 移动文件到另一个文件夹
     */
    @PostMapping("/changeFileFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO changeFileFolder(HttpSession session, @VerifyParam(required = true) String fileIds, @VerifyParam(required = true) String filePid) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.changeFileFolder(fileIds, filePid, webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param fileId
     * @return ResponseVO
     * @description 创建文件下载链接
     */
    @PostMapping("/createDownloadUrl/{fileId}")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        return super.createDownloadUrl(fileId, getUserInfoFromSession(session).getUserId());
    }

    /**
     * @param request
     * @param response
     * @param code
     * @return void
     * @description 下载文件
     */
    @GetMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @description 删除文件
     */
    @PostMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.removeFile2RecycleBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

}

