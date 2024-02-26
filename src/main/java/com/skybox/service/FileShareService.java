package com.skybox.service;

import com.skybox.entity.dto.SessionShareDto;
import com.skybox.entity.po.FileShare;
import com.skybox.entity.query.FileShareQuery;
import com.skybox.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service
 * @ClassName: FileShareService
 * @Datetime: 2023/11/11 21:04
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 定义文件分享相关的业务逻辑方法
 */

public interface FileShareService {

    /**
     * 根据条件查询列表
     */
    List<FileShare> findListByParam(FileShareQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileShareQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<FileShare> findListByPage(FileShareQuery param);

    /**
     * 新增
     */
    Integer add(FileShare bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileShare> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<FileShare> listBean);

    /**
     * 根据ShareId查询对象
     */
    FileShare getFileShareByShareId(String shareId);


    /**
     * 根据ShareId修改
     */
    Integer updateFileShareByShareId(FileShare bean, String shareId);


    /**
     * 根据ShareId删除
     */
    Integer deleteFileShareByShareId(String shareId);

    /**
     * 保存分享
     */
    void saveShare(FileShare share);

    /**
     * 删除共享文件
     */
    void deleteFileShareBatch(String[] shareIdArray, String userId);

    /**
     * 检查共享验证码
     */
    SessionShareDto checkShareCode(String shareId, String code);
}

