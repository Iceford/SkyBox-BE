package com.skybox.service;

import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.UploadResultDto;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service
 * @ClassName: FileInfoService
 * @Datetime: 2023/11/11 20:58
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 定义文件信息相关的业务逻辑方法
 */

public interface FileInfoService {

    /**
     * 根据条件查询列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileInfoQuery param);

    /**
     * 分页查询
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    /**
     * 新增
     */
    Integer add(FileInfo bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<FileInfo> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<FileInfo> listBean);

    /**
     * 根据FileIdAndUserId查询对象
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);


    /**
     * 根据FileIdAndUserId修改
     */
    Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId);


    /**
     * 根据FileIdAndUserId删除
     */
    Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 上传文件
     */
    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex,
                               Integer chunks);

    /**
     * 重命名
     */
    FileInfo rename(String fileId, String userId, String fileName);

    /**
     * 新建文件夹
     */
    FileInfo newFolder(String filePid, String userId, String folderName);

    /**
     * 更改文件文件夹
     */
    void changeFileFolder(String fileIds, String filePid, String userId);

    /**
     * 批量将文件移动到回收站
     */
    void removeFile2RecycleBatch(String userId, String fileIds);

    /**
     * 批量恢复文件
     */
    void recoverFileBatch(String userId, String fileIds);

    /**
     * 批量删除文件
     */
    void delFileBatch(String userId, String fileIds, Boolean adminOp);

    /**
     * 检查根文件Pid
     */
    void checkRootFilePid(String rootFilePid, String userId, String fileId);

    /**
     * 保存分享
     */
    void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String cureentUserId);

    /**
     * 获取用户使用空间
     */
    Long getUserUseSpace(@Param("userId") String userId);

    /**
     * 按用户ID删除文件
     */
    void deleteFileByUserId(@Param("userId") String userId);
}

