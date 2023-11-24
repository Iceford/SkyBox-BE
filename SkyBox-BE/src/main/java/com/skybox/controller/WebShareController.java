package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.controller.commonfilecontroller.CommonFileController;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionShareDto;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.enums.FileDelFlagEnums;
import com.skybox.entity.enums.ResponseCodeEnum;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.po.FileShare;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.FileInfoVO;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.entity.vo.ShareInfoVO;
import com.skybox.exception.BusinessException;
import com.skybox.service.FileShareService;
import com.skybox.service.UserInfoService;
import com.skybox.utils.CopyTools;
import com.skybox.utils.StringTools;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: WebShareController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个WebShareController控制器，用于处理与文件分享相关的HTTP请求
 */

@RestController
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {

    @Resource
    private FileShareService fileShareService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * @param session
     * @param shareId
     * @return ResponseVO
     * @description 获取分享登录信息
     */
    @PostMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareLoginInfo(HttpSession session,
                                        @VerifyParam(required = true) String shareId) {
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        // 判断是否是当前用户分享的文件
        SessionWebUserDto userDto = getUserInfoFromSession(session);

        shareInfoVO.setCurrentUser(userDto != null &&
                userDto.getUserId().equals(shareSessionDto.getShareUserId()));

        return getSuccessResponseVO(shareInfoVO);
    }

    /**
     * @param shareId
     * @return ResponseVO
     * @description 获取分享信息
     */
    @PostMapping("/getShareInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    /**
     * @param shareId
     * @return ShareInfoVO
     * @description 获取分享的详细信息
     */
    private ShareInfoVO getShareInfoCommon(String shareId) {
        // 根据shareId获得FileShare
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        // 如果FileShare为空或者已经过期
        if (share == null || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        // FileShare映射为ShareInfoVO
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(share.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    /**
     * @param session
     * @param shareId
     * @param code
     * @return ResponseVO
     * @description 校验分享码
     */
    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        SessionShareDto shareSessionDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, shareSessionDto);
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param shareId
     * @param filePid
     * @return ResponseVO
     * @description 加载文件列表
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId, String filePid) {
        // 查询出对应链接下的SessionShareDto
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        FileInfoQuery query = new FileInfoQuery();

        // 如果父目录不是根目录
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
//            fileInfoService.checkRootFilePid(shareSessionDto.getFileId(), shareSessionDto.getShareUserId(), filePid);
            query.setFilePid(filePid);
        } else {
            // 如果是根目录
            query.setFileId(shareSessionDto.getFileId());
        }
        query.setUserId(shareSessionDto.getShareUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
    }


    /**
     * @param session
     * @param shareId
     * @return SessionShareDto
     * @description 检查分享是否失效
     */
    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        if (shareSessionDto.getExpireTime() != null && new Date().after(shareSessionDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        return shareSessionDto;
    }

    /**
     * @param session
     * @param shareId
     * @param path
     * @return ResponseVO
     * @description 获取文件夹信息
     */
    @PostMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId,
                                    @VerifyParam(required = true) String path) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.getFolderInfo(path, shareSessionDto.getShareUserId());
    }

    /**
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     * @return void
     * @description 获取文件
     */
    @RequestMapping("/getFile/{shareId}/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session,
                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * @param response
     * @param session
     * @param shareId
     * @param fileId
     * @return void
     * @description 获取视频信息
     */
    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * @param session
     * @param shareId
     * @param fileId
     * @return ResponseVO
     * @description 创建下载链接
     */
    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.createDownloadUrl(fileId, shareSessionDto.getShareUserId());
    }

    /**
     * @param request
     * @param response
     * @param code
     * @return void
     * @description 下载文件
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    /**
     * @param session
     * @param shareId
     * @param shareFileIds
     * @param myFolderId
     * @return ResponseVO
     * @description 保存分享到用户的网盘
     */
    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveShare(HttpSession session,
                                @VerifyParam(required = true) String shareId,
                                @VerifyParam(required = true) String shareFileIds,
                                @VerifyParam(required = true) String myFolderId) {
        // 校验分享链接
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 校验登陆用户和分享用户
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }

        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId,
                shareSessionDto.getShareUserId(), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }

}

