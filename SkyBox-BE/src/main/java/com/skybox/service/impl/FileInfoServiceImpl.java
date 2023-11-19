package com.skybox.service.impl;

import com.skybox.config.AppConfig;
import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionWebUserDto;
import com.skybox.entity.dto.UploadResultDto;
import com.skybox.entity.dto.UserSpaceDto;
import com.skybox.entity.enums.*;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.query.SimplePage;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.exception.BusinessException;
import com.skybox.mappers.FileInfoMapper;
import com.skybox.mappers.UserInfoMapper;
import com.skybox.service.FileInfoService;
import com.skybox.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: FileInfoServiceImpl
 * @Datetime: 2023/11/11 21:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于文件信息相关的业务逻辑
 */

@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    // 文件信息的数据访问对象
    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;
    // 用于操作 Redis 缓存的组件
    @Resource
    private RedisComponent redisComponent;
    // 用户信息的数据访问对象
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    // 应用程序的配置类
    @Resource
    private AppConfig appConfig;
    // 对象自身的引用。使用 @Lazy 注解延迟加载，防止循环依赖
    @Resource
    @Lazy
    private FileInfoServiceImpl fileInfoService;

    /**
     * @param dirPath    目录路径，表示存放文件块的目录
     * @param toFilePath 目标文件路径，表示合并后的文件保存的路径
     * @param fileName   合并后的文件名
     * @param delSource  是否删除源文件块
     * @return void
     * @description 合并指定目录下的多个文件块成为一个文件
     */
    public static void union(String dirPath, String toFilePath, String fileName, boolean delSource) throws BusinessException {
        // 检查目录是否存在，如果目录不存在，则抛出业务异常
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        // 将目标文件路径转换为 File 对象
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        // 创建一个用于写入文件的 RandomAccessFile 对象(RandomAccessFile支持"随机访问"的方式，程序可以直接跳转到文件的任意地方来读写数据)
        RandomAccessFile writeFile = null;
        try {
            // 目标目录
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] b = new byte[1024 * 10];
            // 遍历目录下的所有文件块
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                // 创建读块文件的对象，分别取出 0, 1, 2 ...
                File chunkFile = new File(dirPath + File.separator + i);
                // 创建一个用于读取文件的 RandomAccessFile 对象
                RandomAccessFile readFile = null;
                try {
                    // 循环读取文件块中的数据，并将数据写入目标文件中
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    // 如果在读取或写入过程中发生异常，代码会捕获异常并抛出业务异常
                    log.error("合并分片失败", e);
                    throw new BusinessException("合并文件失败");
                } finally {
                    if (readFile != null) {
                        readFile.close();
                    }
                }
            }
        } catch (Exception e) {
            log.error("合并文件:{}失败", fileName, e);
            throw new BusinessException("合并文件" + fileName + "出错了");
        } finally {
            try {
                // 在所有文件块都处理完毕后，会关闭读写文件的流对象
                if (writeFile != null) {
                    writeFile.close();
                }
            } catch (IOException e) {
                log.error("关闭流失败", e);
            }
            // 如果 delSource 参数为 true，则会删除源文件块目录
            if (delSource) {
                if (dir.exists()) {
                    try {
                        // 以递归方式删除目录
                        FileUtils.deleteDirectory(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return FileInfo
     * @description 根据文件ID和用户ID查询文件信息的方法
     */
    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
        return fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * @param filePid    父文件夹的ID
     * @param userId     用户ID
     * @param folderName 文件夹名称
     * @return FileInfo
     * @description 创建新的文件夹
     */
    @Override
//  @Transactional
    public FileInfo newFolder(String filePid, String userId, String folderName) {
        // 检查指定父文件夹下是否存在同名的文件夹
        checkFileName(filePid, userId, folderName, FileFolderTypeEnums.FOLDER.getType());
        // 创建 FileInfo 对象，并设置文件夹的各个属性，如文件ID、用户ID、父文件夹ID、文件夹名称等
        Date curDate = new Date();
        FileInfo fileInfo = new FileInfo();
        // 生成一个长度为 10 的随机字符串，用作文件的唯一标识
        fileInfo.setFileId(StringTools.getRandomString(Constants.LENGTH_10));
        fileInfo.setUserId(userId);
        fileInfo.setFilePid(filePid);
        fileInfo.setFileName(folderName);
        fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfo.setCreateTime(curDate);
        fileInfo.setLastUpdateTime(curDate);
        fileInfo.setStatus(FileStatusEnums.USING.getStatus());
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        // 将文件夹信息插入到数据库中
        fileInfoMapper.insert(fileInfo);
        // 返回创建的文件夹信息
        return fileInfo;
    }

    /**
     * @param param 文件信息查询参数对象
     * @return List<FileInfo>
     * @description 根据查询参数查询文件信息列表
     */
    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        // 调用 fileInfoMapper 的 selectList 方法，根据查询参数查询数据库中符合条件的文件信息列表，并返回结果
        return fileInfoMapper.selectList(param);
    }

    /**
     * @param userId         用户ID
     * @param filePid        父文件夹的ID
     * @param currentFileIds 当前文件ID列表，用于排除已选择的文件夹
     * @return List<FileInfo>
     * @description 加载指定用户、指定文件夹下的所有文件夹信息
     */
    @Override
    public List<FileInfo> loadAllFolder(String userId, String filePid, String currentFileIds) {
        // 创建 FileInfoQuery 对象，并设置查询参数，包括用户ID、父文件夹ID、排除的文件ID列表等
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFilePid(filePid);
        query.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        if (!StringTools.isEmpty(currentFileIds)) {
            query.setExcludeFileIdArray(currentFileIds.split(","));
        }
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        query.setOrderBy("create_time desc");
        // 调用 findListByParam 方法，根据查询参数查询数据库中符合条件的文件夹信息列表，并返回结果
        return fileInfoService.findListByParam(query);
    }

    /**
     * @param fileIds 选中文件的ID列表，多个文件ID用逗号分隔
     * @param filePid 目标文件夹的ID
     * @param userId  用户ID
     * @return void
     * @description 将选中的文件移动到指定的文件夹
     */
    @Override
    public void changeFileFolder(String fileIds, String filePid, String userId) {
        // 首先检查目标文件夹是否为自身，如果是则抛出业务异常
        if (fileIds.equals(filePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 根据目标文件夹ID和用户ID查询目标文件夹的信息，如果目标文件夹不存在或已删除，则抛出业务异常
        if (!Constants.ZERO_STR.equals(filePid)) {
            FileInfo fileInfo = getFileInfoByFileIdAndUserId(filePid, userId);
            // 当前用户移动到的目录不存在或者不是目录
            if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }
        // 将选中的文件ID列表分割成数组
        // fileIds -> {fileId1,fileId2,fileId3...}
        String[] fileIdArray = fileIds.split(",");

        // 如果移动到的目录正常，查询出toFile下的所有文件
        FileInfoQuery query = new FileInfoQuery();
        query.setFilePid(filePid);
        query.setUserId(userId);
        List<FileInfo> dbFileList = findListByParam(query);

        // 将查询出的list集合收集为以fileName为key, 以集合元素fileInfo为值
        // (file1, file2) -> file2) 如果两个文件名字相同，取第二个
        Map<String, FileInfo> dbFileNameMap = dbFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (file1, file2) -> file2));
        //查询选中的文件
        query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileIdArray);
        List<FileInfo> selectFileList = findListByParam(query);

        //如果存在名字相同，则将所选文件重命名
        for (FileInfo item : selectFileList) {
            FileInfo rootFileInfo = dbFileNameMap.get(item.getFileName());
            FileInfo updateInfo = new FileInfo();
            if (rootFileInfo != null) {
                //文件名已经存在，重命名被还原的文件名
                String fileName = StringTools.rename(item.getFileName());
                updateInfo.setFileName(fileName);
            }
            updateInfo.setFilePid(filePid);
            fileInfoMapper.updateByFileIdAndUserId(updateInfo, item.getFileId(), userId);
        }
    }

    /**
     * @param userId  用户ID
     * @param fileIds 文件ID列表，多个文件ID用逗号分隔
     * @param delFlag 删除标志，表示要查询的文件的删除状态
     * @return List<FileInfo>
     * @description 根据文件ID列表和删除标志查询文件信息列表
     */
    private List<FileInfo> selectListByIdsAndDelFlag(String userId, String fileIds, Integer delFlag) {
        // 根据文件ID列表将其分割成数组
        String[] fileIdArray = fileIds.split(",");
        // 创建一个 FileInfoQuery 对象，并设置查询参数，包括用户ID、文件ID数组和删除标志
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileIdArray);
        query.setDelFlag(delFlag);
        // 调用 fileInfoMapper 的 selectList 方法，根据查询参数查询数据库中符合条件的文件信息列表
        return fileInfoMapper.selectList(query);
    }

    /**
     * @param userId  用户ID
     * @param fileIds 文件ID列表，多个文件ID用逗号分隔
     * @return void
     * @description 批量将文件放入回收站
     */
    @Override
    @Transactional
    public void removeFile2RecycleBatch(String userId, String fileIds) {
        // 查询用户指定文件ID且状态为使用中的文件信息列表
        List<FileInfo> fileInfoList = selectListByIdsAndDelFlag(userId, fileIds, FileDelFlagEnums.USING.getFlag());
        // 如果文件信息列表为空，则直接返回
        if (fileInfoList.isEmpty()) {
            return;
        }
        // 创建一个空的文件夹ID列表 delFilePidList
        List<String> delFilePidList = new ArrayList<>();
        // 如果文件类型为文件夹，调用 findAllSubFolderFileIdList 方法递归找出该文件夹中的所有文件夹，并将它们的文件ID添加到 delFilePidList 中
        fileInfoList.stream().filter(fileInfo -> fileInfo.getFolderType().equals(FileFolderTypeEnums.FOLDER.getType())).forEach(fileInfo -> findAllSubFolderFileIdList(delFilePidList, userId, fileInfo.getFileId(), FileDelFlagEnums.USING.getFlag()));
        // 如果 delFilePidList 不为空，则将目录下的所有文件的删除标志更新为已删除
        if (!delFilePidList.isEmpty()) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setDelFlag(FileDelFlagEnums.DEL.getFlag());
            this.fileInfoMapper.updateFileDelFlagBatch(updateInfo, userId, delFilePidList, null, FileDelFlagEnums.USING.getFlag());
        }
        // 将选中的文件的删除标志更新为回收站，并设置回收时间
        List<String> delFileIdList = Arrays.asList(fileIds.split(","));
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRecoveryTime(new Date());
        fileInfo.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        // 如果选中的文件是文件夹且在根目录下已存在同名文件，则对它们进行重命名
        this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnums.USING.getFlag());
    }

    /**
     * @param userId  用户ID
     * @param fileIds 文件ID列表，多个文件ID用逗号分隔
     * @return void
     * @description 批量还原回收站中的文件
     */
    @Override
    @Transactional
    public void recoverFileBatch(String userId, String fileIds) {
        // 查询用户指定文件ID且状态为回收站的文件信息列表
        List<FileInfo> fileInfoList = selectListByIdsAndDelFlag(userId, fileIds, FileDelFlagEnums.RECYCLE.getFlag());
        // 创建一个空的文件夹ID列表 delFileSubFolderFileIdList
        List<String> delFileSubFolderFileIdList = new ArrayList<>();
        //找到所选文件子目录文件ID

        // 如果文件类型为文件夹，调用 findAllSubFolderFileIdList 方法递归找出该文件夹中的所有文件夹，并将它们的文件ID添加到 delFileSubFolderFileIdList 中
        fileInfoList.stream().filter(fileInfo -> fileInfo.getFolderType().equals(FileFolderTypeEnums.FOLDER.getType())).forEach(fileInfo -> findAllSubFolderFileIdList(delFileSubFolderFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnums.USING.getFlag()));

        // 查询所有根目录下的文件信息列表，准备判断是否需要重命名
        FileInfoQuery query = new FileInfoQuery();
        query = new FileInfoQuery();
        query.setUserId(userId);
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        query.setFilePid(Constants.ZERO_STR);
        List<FileInfo> allRootFileList = this.fileInfoMapper.selectList(query);
        Map<String, FileInfo> rootFileMap = allRootFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (file1, file2) -> file2));

        // 将目录下的所有删除的文件的删除标志更新为正常
        if (!delFileSubFolderFileIdList.isEmpty()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, delFileSubFolderFileIdList, null, FileDelFlagEnums.DEL.getFlag());
        }

        // 将选中的文件的删除标志更新为正常，并将它们的父文件夹ID设置为根目录
        List<String> delFileIdList = Arrays.asList(fileIds.split(","));
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfo.setFilePid(Constants.ZERO_STR);
        fileInfo.setLastUpdateTime(new Date());
        this.fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnums.RECYCLE.getFlag());

        // 对于选中的需还原的文件，如果在根目录中存在同名文件，则进行重命名
        for (FileInfo item : fileInfoList) {
            // 从map中查找名字相同的文件
            FileInfo rootFileInfo = rootFileMap.get(item.getFileName());
            // 文件名已经存在，重命名被还原的文件名
            if (rootFileInfo != null) {
                String fileName = StringTools.rename(item.getFileName());
                FileInfo updateInfo = new FileInfo();
                updateInfo.setFileName(fileName);
                this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, item.getFileId(), userId);
            }
        }
    }

    /**
     * @param userId  用户ID
     * @param fileIds 文件ID列表，多个文件ID用逗号分隔
     * @param adminOp 管理员操作标志，表示是否为管理员操作
     * @return void
     * @description 批量删除文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFileBatch(String userId, String fileIds, boolean adminOp) {
        // 查询用户指定文件ID且状态为回收站的文件信息列表
        List<FileInfo> fileInfoList = selectListByIdsAndDelFlag(userId, fileIds, FileDelFlagEnums.RECYCLE.getFlag());
        // 创建一个空的文件夹ID列表 delFileSubFolderFileIdList
        List<String> delFileSubFolderFileIdList = new ArrayList<>();
        // 如果文件类型为文件夹，调用 findAllSubFolderFileIdList 方法递归找出该文件夹中的所有文件夹，并将它们的文件ID添加到 delFileSubFolderFileIdList 中
        fileInfoList.stream().filter(fileInfo -> fileInfo.getFolderType().equals(FileFolderTypeEnums.FOLDER.getType())).forEach(fileInfo -> findAllSubFolderFileIdList(delFileSubFolderFileIdList, userId, fileInfo.getFileId(), FileDelFlagEnums.DEL.getFlag()));
        // 如果 delFileSubFolderFileIdList 不为空，则调用 fileInfoMapper 的 delFileBatch 方法，删除所选文件及其子目录中的文件。如果 adminOp 为 false，则将它们的删除标志更新为已删除
        if (!delFileSubFolderFileIdList.isEmpty()) {
            this.fileInfoMapper.delFileBatch(userId, delFileSubFolderFileIdList, null, adminOp ? null : FileDelFlagEnums.DEL.getFlag());
        }
        // 调用 fileInfoMapper 的 delFileBatch 方法，删除所选文件。如果 adminOp 为 false，则将它们的删除标志更新为回收站
        this.fileInfoMapper.delFileBatch(userId, null, Arrays.asList(fileIds.split(",")), adminOp ? null : FileDelFlagEnums.RECYCLE.getFlag());
        Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        // 查询用户的使用空间大小，并将其更新到用户信息中
        UserInfo userInfo = new UserInfo();
        userInfo.setUseSpace(useSpace);
        userInfoMapper.updateByUserId(userInfo, userId);
        // 设置用户的使用空间缓存
        UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(userId);
        userSpaceDto.setUseSpace(useSpace);
        redisComponent.saveUserSpaceUse(userId, userSpaceDto);
    }

    /**
     * @param shareRootFilePid 分享的根文件夹ID
     * @param shareFileIds     分享的文件ID列表，多个文件ID用逗号分隔
     * @param myFolderId       目标文件夹ID
     * @param shareUserId      分享文件的用户ID
     * @param currentUserId    当前用户ID
     * @return void
     * @description 保存分享的文件到目标文件夹中
     */
    @Override
    public void saveShare(String shareRootFilePid, String shareFileIds, String myFolderId, String shareUserId, String currentUserId) {
        // 将 shareFileIds 字符串按逗号分隔，得到分享的文件ID数组 shareFileIdArray
        String[] shareFileIdArray = shareFileIds.split(",");
        // 创建一个 FileInfoQuery 对象 fileInfoQuery，设置查询条件为当前用户ID和目标文件夹ID，用于查询目标文件夹下的所有文件
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(currentUserId);
        fileInfoQuery.setFilePid(myFolderId);
        // 目标文件夹下所有文件
        List<FileInfo> currentFileList = this.fileInfoMapper.selectList(fileInfoQuery);
        // 映射成map
        Map<String, FileInfo> currentFileMap = currentFileList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (file1, file2) -> file2));
        // 选择的文件
        fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(shareUserId);
        fileInfoQuery.setFileIdArray(shareFileIdArray);
        // 选中的所有文件
        List<FileInfo> shareFileList = this.fileInfoMapper.selectList(fileInfoQuery);
        // 重命名选择的文件
        List<FileInfo> copyFileList = new ArrayList<>();
        Date curDate = new Date();
        for (FileInfo item : shareFileList) {
            FileInfo haveFile = currentFileMap.get(item.getFileName());
            if (haveFile != null) {
                item.setFileName(StringTools.rename(item.getFileName()));
            }
            // 需要找出所有文件复制
            findAllSubFile(copyFileList, item, shareUserId, currentUserId, curDate, myFolderId);
        }
        this.fileInfoMapper.insertBatch(copyFileList);
    }

    /**
     * @param userId 用户ID
     * @return void
     * @description 根据用户ID删除文件
     */
    @Override
    public void deleteFileByUserId(String userId) {
        // 调用 fileInfoMapper 的 deleteFileByUserId 方法，将根据用户ID删除相关的文件
        this.fileInfoMapper.deleteFileByUserId(userId);
    }

    /**
     * @param copyFileList  文件信息列表，用于存储找到的文件及其子文件
     * @param fileInfo      当前要查找的文件信息对象
     * @param sourceUserId  源文件所属用户的ID
     * @param currentUserId 当前用户的ID
     * @param curDate       当前日期
     * @param newFilePid    新文件的父文件夹ID
     * @return void
     * @description 用于递归查找给定文件及其子文件，并将它们添加到 copyFileList 列表中
     */
    private void findAllSubFile(List<FileInfo> copyFileList, FileInfo fileInfo, String sourceUserId, String currentUserId, Date curDate, String newFilePid) {
        // 将当前文件的信息进行修改，包括设置新的文件ID、父文件夹ID、用户ID，以及更新创建时间和最后更新时间
        String sourceFileId = fileInfo.getFileId();
        fileInfo.setCreateTime(curDate);
        fileInfo.setLastUpdateTime(curDate);
        fileInfo.setFilePid(newFilePid);
        fileInfo.setUserId(currentUserId);
        String newFileId = StringTools.getRandomString(Constants.LENGTH_10);
        fileInfo.setFileId(newFileId);
        // 将修改后的文件信息对象添加到 copyFileList 列表中
        copyFileList.add(fileInfo);
        // 判断当前文件是否为文件夹类型，如果是文件夹，则递归查找其子文件
        if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
            // 创建一个 FileInfoQuery 对象 query，设置查询条件为源文件ID和源用户ID
            FileInfoQuery query = new FileInfoQuery();
            query.setFilePid(sourceFileId);
            query.setUserId(sourceUserId);
            // 调用 fileInfoMapper 的 selectList 方法，查询源文件夹下的文件信息列表，存储在 sourceFileList 中
            List<FileInfo> sourceFileList = this.fileInfoMapper.selectList(query);
            // 对于 sourceFileList 中的每个文件信息对象 item，递归调用 findAllSubFile 方法，传递 copyFileList、item、sourceUserId、currentUserId、curDate 和新文件的ID作为参数
            sourceFileList.forEach(item -> findAllSubFile(copyFileList, item, sourceUserId, currentUserId, curDate, newFileId));
        }
    }

    /**
     * @param fileIdList 文件ID列表，用于存储查找到的文件ID
     * @param userId     用户ID
     * @param fileId     当前文件夹的ID
     * @param delFlag    删除标志，用于筛选文件是否已删除
     * @return void
     * @description 递归查找给定文件夹及其子文件夹的文件ID
     */
    private void findAllSubFolderFileIdList(List<String> fileIdList, String userId, String fileId, Integer delFlag) {
        // 将当前文件夹的ID添加到 fileIdList 列表中，表示将该文件夹及其子文件夹的文件ID都要删除
        fileIdList.add(fileId);
        // 创建一个 FileInfoQuery 对象 query，设置查询条件为指定的用户ID、文件夹ID、删除标志和文件夹类型
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFilePid(fileId);
        query.setDelFlag(delFlag);
        query.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        // 调用 fileInfoMapper 的 selectList 方法，查询当前文件夹下的所有文件夹列表，存储在 fileInfoList 中
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        // 对于 fileInfoList 中的每个文件夹，递归调用 findAllSubFolderFileIdList 方法，传递 fileIdList、userId、该文件夹的ID和删除标志作为参数。这样可以逐层遍历文件夹的子文件夹，将它们的文件ID都添加到 fileIdList 中
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileIdList(fileIdList, userId, fileInfo.getFileId(), delFlag);
        }
    }

    /**
     * @param filePid    父文件夹ID
     * @param userId     用户ID
     * @param fileName   要检查的文件名
     * @param folderType 文件夹类型
     * @return void
     * @description 检查指定文件夹下是否已存在同名文件
     */
    private void checkFileName(String filePid, String userId, String fileName, Integer folderType) {
        // 创建一个 FileInfoQuery 对象 fileInfoQuery，设置查询条件为指定的文件夹类型、文件名、父文件夹ID、用户ID和未删除的标志
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setFolderType(folderType);
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        // 调用 fileInfoMapper 的 selectCount 方法，查询符合条件的文件数量，将结果存储在 count 变量中
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        // 如果查询到的文件数量大于0，表示在该目录下已存在同名文件，抛出自定义的 BusinessException 异常，提示用户修改文件名
        if (count > 0) {
            throw new BusinessException("此目录下已存在同名文件，请修改名称");
        }
    }

    /**
     * @param fileId   文件ID
     * @param userId   用户ID
     * @param fileName 新的文件名
     * @return FileInfo
     * @description 重命名文件
     */
    @Override
    public FileInfo rename(String fileId, String userId, String fileName) {
        // 查询文件信息
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        // 如果文件信息为null，表示文件不存在，会抛出一个BusinessException异常
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        // 检查文件名：检查文件名是否有效，确保在父文件夹中不会存在同名文件
        String filePid = fileInfo.getFilePid();
        checkFileName(filePid, userId, fileName, fileInfo.getFolderType());
        // 处理文件名：如果文件类型是文件（而不是文件夹），则在新的文件名后面添加原始文件名的后缀，以保持文件类型的完整性
        if (FileFolderTypeEnums.FILE.getType().equals(fileInfo.getFolderType())) {
            fileName = fileName + StringTools.getFileSuffix(fileInfo.getFileName());
        }
        // 更新文件信息：创建一个新的FileInfo对象dbInfo，设置其文件名为新的文件名fileName，并将最后更新时间设置为当前时间curDate
        Date curDate = new Date();
        FileInfo dbInfo = new FileInfo();
        dbInfo.setFileName(fileName);
        dbInfo.setLastUpdateTime(curDate);
        // 将dbInfo对象的信息更新到数据库中，使用fileId和userId作为查询条件
        fileInfoMapper.updateByFileIdAndUserId(dbInfo, fileId, userId);
        fileInfo.setFileName(fileName);
        fileInfo.setLastUpdateTime(curDate);
        // 返回结果：将fileInfo对象的文件名和最后更新时间设置为新的值，并将其作为方法的返回值返回
        return fileInfo;
    }

    /**
     * @param param
     * @return PaginationResultVO<FileInfo>
     * @description 用于按页查询文件列表并返回结果
     */
    @Override
    public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
        // 记录条数：根据param参数查询满足条件的文件记录的总数，并将结果赋给count变量
        int count = fileInfoMapper.selectCount(param);
        // 页面大小：接下来，通过检查param参数中的pageSize字段，确定页面的大小。如果param中的pageSize为null，则使用默认值15作为页面大小
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
        /**
         * param.getPageNo() 第几页
         * count 共多少条记录
         * pageSize 一页显示多少页
         */
        // 分页查询：使用param.getPageNo()指定的页码、count总记录数和pageSize页面大小，创建一个SimplePage对象page，用于表示分页信息
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        // 查询文件列表：通过调用fileInfoMapper对象的selectList方法，根据param参数查询符合条件的文件列表，并将结果赋给list变量
        List<FileInfo> list = fileInfoMapper.selectList(param);
        // 构建分页结果
        PaginationResultVO<FileInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * @param webUserDto 包含当前用户信息的DTO对象
     * @param fileId     文件ID
     * @param file       上传的文件
     * @param fileName   文件名
     * @param filePid    文件父ID
     * @param fileMd5    文件MD5值
     * @param chunkIndex 当前分片索引
     * @param chunks     总分片数
     * @return UploadResultDto
     * @description 用于处理文件上传操作并返回上传结果
     */
    @Override
    @Transactional
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        // 上传结果对象：创建一个UploadResultDto对象uploadResultDto，用于封装上传结果
        UploadResultDto uploadResultDto = new UploadResultDto();
        // 初始化变量：创建一个File对象tempFileFolder用于存储临时文件夹，创建一个Date对象curDate表示当前日期
        boolean uploadSuccess = true;
        File tempFileFolder = null;
        Date curDate = new Date();
        try {
            // 判断文件ID：如果fileId为空，则生成一个随机字符串作为文件ID，并将其设置到uploadResultDto的fileId字段中
            if (StringTools.isEmpty(fileId)) {
                fileId = StringTools.getRandomString(Constants.LENGTH_10);
            }
            uploadResultDto.setFileId(fileId);
            // 获取用户可用空间：通过调用redisComponent.getUserSpaceUse方法，根据webUserDto对象中的用户ID获取用户可用空间信息，并将结果赋给userSpaceDto对象
            String userId = webUserDto.getUserId();
            UserSpaceDto userSpaceDto = redisComponent.getUserSpaceUse(userId);
            // 判断是否可秒传：如果chunkIndex等于0，表示当前上传的是第一个分片，这时需要判断文件是否可以进行秒传
            if (chunkIndex == 0) {
                // 封装查询条件
                FileInfoQuery fileInfoQuery = new FileInfoQuery();
                // 文件MD5值
                fileInfoQuery.setFileMd5(fileMd5);
                // 只取第一条记录
                fileInfoQuery.setSimplePage(new SimplePage(0, 1));
                // 文件状态为正在使用
                fileInfoQuery.setStatus(FileStatusEnums.USING.getStatus());
                // 执行查询操作
                List<FileInfo> dbFileList = fileInfoMapper.selectList(fileInfoQuery);
                // 如果dbFileList不为空，说明数据库中已存在相同MD5值的文件，可以进行秒传操作
                if (!dbFileList.isEmpty()) {
                    // 从dbFileList中获取第一个文件对象dbFile
                    FileInfo dbFile = dbFileList.get(0);
                    // 判断文件大小是否超出用户可用空间
                    if (dbFile.getFileSize() + userSpaceDto.getUseSpace() > userSpaceDto.getTotalSpace()) {
                        // 如果超出，则抛出异常表示空间不足
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }
                    // 更新dbFile的相关字段，包括文件ID、文件父ID、用户ID、创建时间、最后更新时间、文件状态等
                    dbFile.setFileId(fileId);
                    dbFile.setFilePid(filePid);
                    dbFile.setUserId(userId);
                    dbFile.setCreateTime(curDate);
                    dbFile.setLastUpdateTime(curDate);
                    dbFile.setStatus(FileStatusEnums.USING.getStatus());
                    dbFile.setDelFlag(FileDelFlagEnums.USING.getFlag());
                    dbFile.setFileMd5(fileMd5);
                    // 将文件自动重命名后的文件名设置到dbFile的fileName字段中
                    fileName = autoRename(filePid, userId, fileName);
                    dbFile.setFileName(fileName);
                    // 通过调用fileInfoMapper.insert方法将更新后的文件信息插入数据库中
                    fileInfoMapper.insert(dbFile);
                    uploadResultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                    // 设置uploadResultDto的状态为秒传成功，并更新用户使用空间后返回uploadResultDto
                    updateUserSpace(webUserDto, dbFile.getFileSize());
                    return uploadResultDto;
                }
            }

            // 判断磁盘空间：根据当前分片的大小、当前临时文件夹的大小以及用户已使用的空间大小，判断磁盘空间是否足够存储文件
            Long currentTempSize = redisComponent.getFileTempSize(userId, fileId);
            if (file.getSize() + currentTempSize + userSpaceDto.getUseSpace() > userSpaceDto.getTotalSpace()) {
                // 如果空间不足，则抛出异常表示空间不足
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }

            // 创建临时文件夹：根据配置文件中指定的临时文件夹路径和当前用户ID和文件ID创建临时文件夹
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            // userId + fileId
            String currentUserFolderName = webUserDto.getUserId() + fileId;

            // 保存分片文件：根据当前分片索引和临时文件夹的路径，创建一个新的文件对象newFile，并将上传的文件内容保存到该文件中
            tempFileFolder = new File(tempFolderName + "/" + currentUserFolderName);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }
            File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);

            // 更新临时文件大小：通过调用redisComponent.saveFileTempSize方法，将当前用户ID、文件ID和分片文件的大小保存到Redis中
            redisComponent.saveFileTempSize(userId, fileId, file.getSize());
            // 判断是否为最后一个分片：如果当前分片索引小于总分片数减1，表示还有分片未上传完成，设置uploadResultDto的状态为上传中并返回uploadResultDto
            if (chunkIndex < chunks - 1) {
                // 处理最后一个分片：如果当前分片索引等于总分片数减1，表示所有分片已上传完成。
                uploadResultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
                return uploadResultDto;
            }
            // 根据当前日期生成一个表示月份的字符串month。然后，通过文件名获取文件后缀名，并根据后缀名从枚举类中获取文件类型。接下来，根据文件父ID、用户ID和文件名等信息，创建一个FileInfo对象fileInfo，并设置相关字段续
            String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
            String fileSuffix = StringTools.getFileSuffix(fileName);
            // userId + fileId.fileSuffix
            String realFileName = currentUserFolderName + fileSuffix;
            // 根据后缀从枚举类中获取文件类别
            FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            //插入文件信息：将fileInfo对象插入数据库中，包括文件ID、用户ID、文件MD5值、文件名、文件路径、文件父ID、创建时间、最后更新时间、文件类别、文件类型、文件状态、文件夹类型等
            fileName = autoRename(filePid, userId, fileName);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setUserId(userId);
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(fileName);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(curDate);
            fileInfo.setLastUpdateTime(curDate);
            fileInfo.setFileCategory(fileTypeEnums.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnums.getType());
            fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
            fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            this.fileInfoMapper.insert(fileInfo);
            Long totalSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            // 更新用户空间：通过调用updateUserSpace方法，更新用户的使用空间，传入webUserDto和总分片大小
            updateUserSpace(webUserDto, totalSize);
            // 设置上传结果状态：将uploadResultDto的状态设置为上传完成
            uploadResultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
            // 事务提交后执行：通过TransactionSynchronizationManager.registerSynchronization方法注册一个事务同步器，在事务提交后执行指定的操作。在这里，注册的同步器会调用fileInfoService.transferFile方法来处理文件的转移，并通过redisComponent.removeFileTempSize方法移除Redis中保存的临时文件大小
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileInfo.getFileId(), webUserDto);
                    redisComponent.removeFileTempSize(userId, fileInfo.getFileId());
                }
            });

            // 异常处理：如果出现BusinessException异常，表示文件上传失败，记录错误日志并抛出异常。如果出现其他异常，也记录错误日志
        } catch (BusinessException e) {
            log.error("文件上传失败");
            uploadSuccess = false;
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败");
            uploadSuccess = false;
            // 最后处理：如果上传失败且临时文件夹对象tempFileFolder不为空，则尝试删除临时文件夹
        } finally {
            if (tempFileFolder != null && !uploadSuccess) {
                try {
                    FileUtils.deleteDirectory(tempFileFolder);
                } catch (IOException e) {
                    log.error("删除临时目录失败");
                }
            }
        }
        // 返回上传结果：返回uploadResultDto对象，包含上传结果的状态信息
        return uploadResultDto;

    }

    /**
     * @param fileId     文件ID
     * @param webUserDto 当前用户信息的DTO对象
     * @return void
     * @description 用于处理文件的转码操作
     */
    @Async  // 异步执行，不会阻塞当前线程
    public void transferFile(String fileId, SessionWebUserDto webUserDto) {
        // 初始化变量
        boolean transferSuccess = true;     // 文件转码是否成功
        String targetFilePath = null;       // 目标文件路径
        String cover = null;                // 文件封面路径
        FileTypeEnums fileTypeEnum = null;  // 文件类型枚举
        // 查询文件信息：通过调用fileInfoMapper.selectByFileIdAndUserId方法，根据文件ID和用户ID查询文件信息，并将结果赋给fileInfo对象
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, webUserDto.getUserId());
        try {
            // 判断文件信息：如果fileInfo为空或文件状态不为转码状态，则直接返回
            if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            // 创建临时文件夹：首先，根据配置文件中指定的临时文件夹路径和当前用户ID和文件ID创建临时文件夹。如果临时文件夹不存在，则创建该文件夹
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            // 获取文件后缀名和月份：根据文件名获取文件后缀名，并根据文件创建时间格式化得到表示月份的字符串month
            String currentUserFolderName = webUserDto.getUserId() + fileId;
            File fileFolder = new File(tempFolderName + "/" + currentUserFolderName);
            // 如果临时文件夹不存在，则创建该文件夹
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            // 构建目标文件的路径，路径包括目标文件夹路径、月份和真实文件名（由用户ID和文件ID组成）
            String fileSuffix = StringTools.getFileSuffix(fileInfo.getFileName());
            String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
            // 目标目录 e:/webser/web_app/skybox + /file + /{month}
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFolder = new File(targetFolderName + "/" + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            // 真实文件名 {userId+fileId}
            String realFileName = currentUserFolderName + fileSuffix;
            // 真实文件路径
            targetFilePath = targetFolder.getPath() + "/" + realFileName;
            // 将临时文件夹中的文件合并成目标文件，使用了名为"union"的方法，将临时文件夹中的文件合并为目标文件，并删除源文件
            /**
             * fileFolder.getPath() 临时目录
             * targetFilePath 目标目录
             * fileInfo.getFileName() 文件名
             * delSource
             */
            union(fileFolder.getPath(), targetFilePath, fileInfo.getFileName(), true);
            fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            if (FileTypeEnums.VIDEO == fileTypeEnum) {
                // 根据文件后缀名判断文件类型
                cutFile4Video(fileId, targetFilePath);
                // 如果是视频文件，则进行视频文件的切割和生成缩略图的操作
                cover = month + "/" + currentUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolderName + "/" + cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (FileTypeEnums.IMAGE == fileTypeEnum) {
                // 如果文件类型是图片，则生成缩略图，并检查是否成功生成缩略图
                cover = month + "/" + realFileName.replace(".", "_.");
                String coverPath = targetFolderName + "/" + cover;
                Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath), false);
                // 如果没有成功生成，则将原图复制为缩略图
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            // 如果在上述过程中发生异常，会记录错误日志，并将文件转码状态设置为转码失败
            log.error("文件转码失败，文件Id:{},userId:{}", fileId, webUserDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            // 更新文件信息，包括文件大小、文件封面和文件状态
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFileSize(new File(targetFilePath).length());
            updateInfo.setFileCover(cover);
            // 将文件状态从转码状态更新为使用中状态或转码失败状态
            updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId, webUserDto.getUserId(), updateInfo, FileStatusEnums.TRANSFER.getStatus());
        }
    }

    /**
     * @param fileId
     * @param videoFilePath
     * @return void
     * @description 用于视频文件切片的方法，它将给定的视频文件进行切片，并生成相应的索引文件和切片文件
     */
    private void cutFile4Video(String fileId, String videoFilePath) {
        // 根据视频文件路径获取切片目录的路径，即去除文件后缀名的部分
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        // 如果切片目录不存在，则创建该目录
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        // 定义两个命令字符串，用于执行FFmpeg命令
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s"; // 用于将视频文件转换为TS格式的文件
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts"; // 用于将TS文件进行切片
        // 构建TS文件的路径，命名为"index.ts"，路径由切片目录和常量"TS_NAME"组成
        String tsPath = tsFolder + "/" + Constants.TS_NAME;
        // 使用FFmpeg执行CMD_TRANSFER_2TS命令，将视频文件转换为TS格式的文件。该命令会复制视频和音频流，并将视频文件转换为H.264 Annex B格式的TS文件
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        // 生成索引文件和切片文件的命令使用CMD_CUT_TS字符串构建，其中包括输入的TS文件路径、索引文件的路径、切片目录的路径以及文件ID作为切片文件的前缀
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath(), fileId);
        // 使用FFmpeg执行CMD_CUT_TS命令，将TS文件切片为固定时长的小片段。命令中的-segment_time 30表示每个切片的时长为30秒
        ProcessUtils.executeCommand(cmd, false);
        // 在切片完成后，删除生成的"index.ts"文件，因为它在生成索引文件和切片文件后不再需要
        new File(tsPath).delete();
    }

    /**
     * @param webUserDto SessionWebUserDto对象，包含用户的相关信息
     * @param useSpace Long类型，表示要更新的空间使用量
     * @return void
     * @description 更新用户的空间使用情况
     */
    private void updateUserSpace(SessionWebUserDto webUserDto, Long useSpace) {
        Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSpace, null);
        // 如果更新的记录数为0，表示更新失败，即使用空间超过了总空间
        if (count == 0) {
            // 抛出一个名为BusinessException的异常，异常的错误代码为"CODE_904"，用于表示使用空间超过限制的错误
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }
        // 如果更新的记录数大于0，表示更新成功。接下来，从webUserDto中获取用户ID，并使用该ID从Redis中获取用户空间使用情况的DTO对象spaceDto
        String userId = webUserDto.getUserId();
        // 如果更新的记录数大于0，表示更新成功。接下来，从webUserDto中获取用户ID，并使用该ID从Redis中获取用户空间使用情况的DTO对象spaceDto
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(userId);
        // 将用户实际使用空间大小useSpace添加到spaceDto的useSpace属性中，表示更新后的使用空间大小
        spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
        // 使用redisComponent将更新后的用户空间使用情况保存回Redis中，以便进行缓存更新
        redisComponent.saveUserSpaceUse(userId, spaceDto);

    }

    /**
     * @param filePid  文件父ID
     * @param userId   用户ID
     * @param fileName 文件名
     * @return String
     * @description 自动重命名文件
     */
    private String autoRename(String filePid, String userId, String fileName) {
        // 封装查询条件，如果查询出数据，则需要重命名
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfoQuery.setFileName(fileName);
        // 使用fileInfoMapper的selectCount方法执行查询操作，统计满足查询条件的记录数
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        // 如果记录数count大于0，表示存在相同的文件名，需要进行重命名
        if (count > 0) {
            // 调用StringTools.rename方法生成一个新的文件名
            return StringTools.rename(fileName);
        }
        // 如果记录数count等于0，表示不存在相同的文件名，直接返回原始的文件名fileName
        return fileName;
    }

}

