package com.skybox.mappers;

import com.skybox.entity.po.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.mappers
 * @ClassName: FileInfoMapper
 * @Datetime: 2023/11/11 20:50
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 针对文件信息的数据库操作接口
 */

public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * @param t      要更新的对象
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据文件ID和用户ID更新数据库记录
     */
    Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据文件ID和用户ID删除数据库记录
     */
    Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return T 一个泛型类型 T 的对象
     * @description 根据文件ID和用户ID查询数据库记录并返回对象
     */
    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * @param fileId    文件ID
     * @param userId    用户ID
     * @param t         要更新的对象
     * @param oldStatus 旧的状态值
     * @return void
     * @description 根据文件ID、用户ID、旧的状态值和对象更新文件状态
     */
    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId, @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

    /**
     * @param fileInfo    文件信息对象
     * @param userId      用户ID
     * @param filePidList 文件父ID列表
     * @param fileIdList  文件ID列表
     * @param oldDelFlag  旧的删除标志位
     * @return void
     * @description 批量更新文件的删除标志位
     */
    void updateFileDelFlagBatch(@Param("bean") FileInfo fileInfo, @Param("userId") String userId, @Param("filePidList") List<String> filePidList, @Param("fileIdList") List<String> fileIdList, @Param("oldDelFlag") Integer oldDelFlag);

    /**
     * @param userId      用户ID
     * @param filePidList 文件父ID列表
     * @param fileIdList  文件ID列表
     * @param oldDelFlag  旧的删除标志位
     * @return void
     * @description 批量删除文件
     */
    void delFileBatch(@Param("userId") String userId, @Param("filePidList") List<String> filePidList, @Param("fileIdList") List<String> fileIdList, @Param("oldDelFlag") Integer oldDelFlag);

    /**
     * @param userId 用户ID
     * @return Long 一个 Long 类型的值，表示已使用的存储空间
     * @description 根据用户ID查询已使用的存储空间
     */
    Long selectUseSpace(@Param("userId") String userId);

    /**
     * @param userId 用户ID
     * @return void
     * @description 根据用户ID删除文件
     */
    void deleteFileByUserId(@Param("userId") String userId);

}

