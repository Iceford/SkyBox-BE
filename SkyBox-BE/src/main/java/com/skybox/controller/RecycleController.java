package com.skybox.controller;

import com.skybox.annotation.GlobalInterceptor;
import com.skybox.annotation.VerifyParam;
import com.skybox.controller.basecontroller.BaseController;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.enums.FileDelFlagEnums;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.FileInfoVO;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.entity.vo.ResponseVO;
import com.skybox.service.FileInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.controller
 * @ClassName: RecycleController
 * @Datetime: 2023/11/12 20:59
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 一个RESTful风格的控制器(Controller)，用于处理回收站相关的HTTP请求
 */

@RestController
@RequestMapping("/recycle")
public class RecycleController extends BaseController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * @param session
     * @param pageNo
     * @param pageSize
     * @return ResponseVO
     * @description 加载回收站文件列表
     */
    @PostMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        query.setPageSize(pageSize);
        query.setPageNo(pageNo);
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("recovery_time desc");
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        PaginationResultVO result = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @description 将回收站文件还原到根目录
     */
    @RequestMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO recoverFile(HttpSession session,
                                  @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.recoverFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    /**
     * @param session
     * @param fileIds
     * @return ResponseVO
     * @description 彻底删除回收站文件
     */
    @PostMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session,
                              @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds, false);
        return getSuccessResponseVO(null);
    }

}

