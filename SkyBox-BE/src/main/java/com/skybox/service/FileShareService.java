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
     * @param param 文件分享查询参数
     * @return List<FileShare>
     * @description 根据参数查询文件分享列表
     */
    List<FileShare> findListByParam(FileShareQuery param);

    /**
     * @param param 文件分享查询参数
     * @return Integer
     * @description 根据参数查询文件分享数量
     */
    Integer findCountByParam(FileShareQuery param);

    /**
     * @param param 文件分享查询参数
     * @return PaginationResultVO<FileShare>
     * @description 分页查询文件分享列表
     */
    PaginationResultVO<FileShare> findListByPage(FileShareQuery param);

    /**
     * @param bean 文件分享对象
     * @return Integer
     * @description 新增文件分享
     */
    Integer add(FileShare bean);

    /**
     * @param listBean 文件分享对象列表
     * @return Integer
     * @description 批量新增多个文件分享
     */
    Integer addBatch(List<FileShare> listBean);

    /**
     * @param listBean 文件分享对象列表
     * @return Integer
     * @description 批量新增或修改多个文件分享
     */
    Integer addOrUpdateBatch(List<FileShare> listBean);

    /**
     * @param shareId 分享ID
     * @return FileShare
     * @description 根据分享ID查询文件分享对象
     */
    FileShare getFileShareByShareId(String shareId);

    /**
     * @param bean 文件分享对象
     * @param shareId 分享ID
     * @return Integer
     * @description 修改指定分享ID的文件分享
     */
    Integer updateFileShareByShareId(FileShare bean, String shareId);

    /**
     * @param shareId 分享ID
     * @return Integer
     * @description 删除指定分享ID的文件分享
     */
    Integer deleteFileShareByShareId(String shareId);

    /**
     * @param shareIdArray 分享ID数组
     * @param userId 用户ID
     * @return void
     * @description 批量删除多个文件分享
     */
    void deleteFileShareBatch(String[] shareIdArray, String userId);

    /**
     * @param share 文件分享对象
     * @return void
     * @description 保存文件分享信息
     */
    void saveShare(FileShare share);

    /**
     * @param shareId 分享ID
     * @param code 待验证的分享码
     * @return SessionShareDto
     * @description 验证用户输入的分享码是否正确，并返回相关的分享信息
     */
    SessionShareDto checkShareCode(String shareId, String code);
}

