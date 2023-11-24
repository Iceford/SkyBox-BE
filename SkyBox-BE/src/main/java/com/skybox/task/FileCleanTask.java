package com.skybox.task;

import com.skybox.entity.enums.FileDelFlagEnums;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.service.FileInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.task
 * @ClassName: FileCleanTask
 * @Datetime: 2023/11/12 21:07
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 定时任务类定期执行清理任务，查询回收站中的过期文件，并删除这些文件
 */

@Component
public class FileCleanTask {
    // 注入的 FileInfoService 对象，用于处理文件信息的服务
    @Resource
    private FileInfoService fileInfoService;

    /**
     * @param
     * @return void
     * @description 使用@Scheduled注解指定了任务的执行时间间隔为3分钟（1000 * 60 * 3毫秒）。该方法会定期执行清理任务
     */
    @Scheduled(fixedDelay = 1000 * 60 * 3)
    // 查询标记为回收站状态的过期文件（根据 FileDelFlagEnums.RECYCLE 标记和过期标记进行过滤），并将它们从系统中删除
    public void execute() {
        // 创建查询条件: 创建一个 FileInfoQuery 对象，并设置 delFlag 属性为回收站的标记，并将 queryExpire 属性设置为 true，表示要查询过期的文件
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        fileInfoQuery.setQueryExpire(true);
        // 查询文件列表: 调用 fileInfoService 的 findListByParam() 方法，根据查询条件获取文件列表
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);
        // 分组文件列表: 将文件列表按照用户ID进行分组，使用 Collectors.groupingBy() 方法
        Map<String, List<FileInfo>> fileInfoMap = fileInfoList.stream()
                .collect(Collectors.groupingBy(FileInfo::getUserId));
        // 遍历分组后的结果: 对于每个用户ID，获取对应的文件ID列表，并将其删除，调用 fileInfoService 的 delFileBatch() 方法
        for (Map.Entry<String, List<FileInfo>> entry : fileInfoMap.entrySet()) {
            List<String> fileIds = entry.getValue().stream().
                    map(FileInfo::getFileId).collect(Collectors.toList());
            fileInfoService.delFileBatch(entry.getKey(), String.join(",", fileIds), false);
        }
    }
}

