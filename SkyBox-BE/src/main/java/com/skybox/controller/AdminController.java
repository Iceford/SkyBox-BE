package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.controller.commonfilecontroller.CommonFileController;
import com.skybox.entity.dto.SysSettingsDto;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.entity.vo.UserInfoVO;
import com.skybox.service.FileInfoService;
import com.skybox.service.UserInfoService;
import com.skybox.utils.RedisComponent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: AdminController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于处理与管理员相关的请求
 */

@RestController
@RequestMapping("/admin")
public class AdminController extends CommonFileController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private FileInfoService fileInfoService;

    /**
     * @param
     * @return ResponseVO
     * @description 获取系统设置信息
     */
    @RequestMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getSysSettings() {
        return getSuccessResponseVO(redisComponent.getSysSettingsDto());
    }

    /**
     * @param registerEmailTitle
     * @param registerEmailContent
     * @param userInitUseSpace
     * @return ResponseVO
     * @description 保存系统设置信息
     */
    @RequestMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO saveSysSettings(@VerifyParam(required = true) String registerEmailTitle, @VerifyParam(required = true) String registerEmailContent, @VerifyParam(required = true) Integer userInitUseSpace) {
        SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterEmailContent(registerEmailContent);
        sysSettingsDto.setUserInitUseSpace(userInitUseSpace);
        redisComponent.saveSysSettingsDto(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    /**
     * @param userInfoQuery
     * @return ResponseVO
     * @description 加载用户列表
     */
    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadUser(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        PaginationResultVO resultVO = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, UserInfoVO.class));
    }

    /**
     * @param userId
     * @param status
     * @return ResponseVO
     * @description 更新用户状态
     */
    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserStatus(@VerifyParam(required = true) String userId, @VerifyParam(required = true) Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }

    /**
     * @param userId
     * @param changeSpace
     * @return ResponseVO
     * @description 更新用户空间
     */
    @RequestMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserSpace(@VerifyParam(required = true) String userId, @VerifyParam(required = true) Integer changeSpace) {
        userInfoService.changeUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    /**
     * @param query
     * @return ResponseVO
     * @description 加载文件列表
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadDataList(FileInfoQuery query) {
        query.setOrderBy("last_update_time desc");
        query.setQueryNickName(true);
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @param path
     * @return ResponseVO
     * @description 获取文件夹信息
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getFolderInfo(@VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, null);
    }

    /**
     * @param response
     * @param userId
     * @param fileId
     * @return void
     * @description 获取文件
     */
    @RequestMapping("/getFile/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getFile(HttpServletResponse response, @PathVariable("userId") @VerifyParam(required = true) String userId, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }

    /**
     * @param response
     * @param userId
     * @param fileId
     * @return void
     * @description 获取视频信息
     */
    @RequestMapping("/ts/getVideoInfo/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getVideoInfo(HttpServletResponse response, @PathVariable("userId") @VerifyParam(required = true) String userId, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        super.getFile(response, fileId, userId);
    }

    /**
     * @param userId
     * @param fileId
     * @return ResponseVO
     * @description 创建下载链接
     */
    @RequestMapping("/createDownloadUrl/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO createDownloadUrl(@PathVariable("userId") @VerifyParam(required = true) String userId, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        return super.createDownloadUrl(fileId, userId);
    }

    /**
     * @param request
     * @param response
     * @param code
     * @return void
     * @description 处理下载文件请求
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * @param fileIdAndUserIds
     * @return ResponseVO
     * @description 删除文件
     */
    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO delFile(@VerifyParam(required = true) String fileIdAndUserIds) {
        String[] fileIdAndUserIdArray = fileIdAndUserIds.split(",");
        for (String fileIdAndUserId : fileIdAndUserIdArray) {
            String[] itemArray = fileIdAndUserId.split("_");
            fileInfoService.delFileBatch(itemArray[0], itemArray[1], true);
        }
        return getSuccessResponseVO(null);
    }
}

