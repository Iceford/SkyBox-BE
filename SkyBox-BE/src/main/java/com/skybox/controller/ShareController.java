package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.controller.basecontroller.BaseController;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.po.FileShare;
import com.skybox.entity.query.FileShareQuery;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.service.FileShareService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: ShareController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个RESTful风格的控制器(Controller)，用于处理文件分享相关的HTTP请求
 */

@RestController("shareController")
@RequestMapping("/share")
public class ShareController extends BaseController {

    @Resource
    private FileShareService fileShareService;

    /**
     * @param session
     * @param query
     * @return ResponseVO
     * @description 加载文件分享列表
     */
    @PostMapping("/loadShareList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadShareList(HttpSession session, FileShareQuery query) {
        query.setOrderBy("share_time desc");
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        query.setUserId(userDto.getUserId());
        query.setQueryFileName(true);
        PaginationResultVO resultVO = this.fileShareService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @param session
     * @param fileId
     * @param validType
     * @param code
     * @return ResponseVO
     * @description 分享文件
     */
    @PostMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO shareFile(HttpSession session, @VerifyParam(required = true) String fileId, @VerifyParam(required = true) Integer validType, String code) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        FileShare share = new FileShare();
        share.setFileId(fileId);
        share.setValidType(validType);
        share.setCode(code);
        share.setUserId(userDto.getUserId());
        fileShareService.saveShare(share);
        return getSuccessResponseVO(share);
    }

    /**
     * @param session
     * @param shareIds
     * @return ResponseVO
     * @description 取消文件分享
     */
    @PostMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO cancelShare(HttpSession session, @VerifyParam(required = true) String shareIds) {
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        fileShareService.deleteFileShareBatch(shareIds.split(","), userDto.getUserId());
        return getSuccessResponseVO(null);
    }
}

