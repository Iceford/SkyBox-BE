package com.skybox.service;

import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.UploadResultDto;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
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
     * @param fileId 文件ID
     * @param userId 用户ID
     * @param fileName 新的文件名
     * @return FileInfo
     * @description 将文件重命名为指定的文件名
     */
    FileInfo rename(String fileId, String userId, String fileName);

    /**
     * @param param 文件信息查询参数
     * @return PaginationResultVO<FileInfo> 一个 PaginationResultVO 对象，包含分页查询结果的文件信息列表
     * @description 分页查询文件列表
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    /**
     * @param webUserDto 用户信息
     * @param fileId 文件ID
     * @param file 上传的文件
     * @param fileName 文件名
     * @param filePid 文件所属父文件夹ID
     * @param fileMd5 文件的MD5值
     * @param chunkIndex 文件块索引
     * @param chunks 总块数
     * @return UploadResultDto
     * @description 处理文件上传操作并返回上传结果
     */
    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    /**
     * @param realFileId 文件的真实ID
     * @param userId 用户ID
     * @return FileInfo
     * @description 通过文件ID和用户ID获取文件信息
     */
    FileInfo getFileInfoByFileIdAndUserId(String realFileId, String userId);

    /**
     * @param filePid 父文件夹ID
     * @param userId 用户ID
     * @param fileName 文件夹名字
     * @return FileInfo
     * @description 在指定的父文件夹下创建新的文件夹
     */
    FileInfo newFolder(String filePid, String userId, String fileName);

    /**
     * @param param 文件信息查询参数
     * @return List<FileInfo>
     * @description 根据参数查询文件列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * @param userId 用户ID
     * @param filePid 文件夹ID
     * @param currentFileIds 当前文件ID列表
     * @return List<FileInfo>
     * @description 加载指定用户和指定文件夹下的所有文件夹
     */
    List<FileInfo> loadAllFolder(String userId, String filePid, String currentFileIds);

    /**
     * @param fileIds 文件ID列表
     * @param filePid 目标文件夹ID
     * @param userId 用户ID
     * @return void
     * @description 将指定的文件移动到目标文件夹中
     */
    void changeFileFolder(String fileIds, String filePid, String userId);

    /**
     * @param userId 用户ID
     * @param fileIds 文件ID列表
     * @return void
     * @description 将指定用户的多个文件移动到回收站
     */
    void removeFile2RecycleBatch(String userId, String fileIds);

    /**
     * @param userId 用户ID
     * @param fileIds 文件ID列表
     * @return void
     * @description 批量恢复回收站中的文件
     */
    void recoverFileBatch(String userId, String fileIds);

    /**
     * @param userId 用户ID
     * @param fileIds 文件ID列表
     * @param b 一个布尔值
     * @return void
     * @description 批量删除用户的多个文件，可以选择是否彻底删除
     */
    void delFileBatch(String userId, String fileIds, boolean b);

    /**
     * @param shareRootFilePid 分享的根文件夹ID
     * @param shareFileIds 分享的文件ID列表
     * @param myFolderId 我的文件夹ID
     * @param shareUserId 分享用户ID
     * @param currentUserId 当前用户ID
     * @return void
     * @description 保存文件分享信息
     */
    void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String currentUserId);

    /**
     * @param userId 用户ID
     * @return void
     * @description 于删除指定用户的所有文件
     */
    void deleteFileByUserId(String userId);

}