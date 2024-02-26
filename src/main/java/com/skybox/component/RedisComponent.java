package com.skybox.component;

import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.DownloadFileDto;
import com.skybox.entity.dto.SysSettingsDto;
import com.skybox.entity.dto.UserSpaceDto;
import com.skybox.entity.po.FileInfo;
import com.skybox.entity.po.UserInfo;
import com.skybox.entity.query.FileInfoQuery;
import com.skybox.entity.query.UserInfoQuery;
import com.skybox.mappers.FileInfoMapper;
import com.skybox.mappers.UserInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.utils
 * @ClassName: RedisComponent
 * @Datetime: 2023/11/12 21:27
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于操作Redis缓存
 */

@Component("redisComponent")
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    /**
     * @param
     * @return SysSettingsDto
     * @description 获取系统设置
     */
    public SysSettingsDto getSysSettingsDto() {
        // 尝试从Redis缓存中获取SysSettingsDto对象
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            // 如果缓存中不存在，则创建一个新的SysSettingsDto对象，并将其存储在Redis缓存中
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * @param sysSettingsDto
     * @return void
     * @description 保存系统设置
     */
    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        // 将传入的SysSettingsDto对象存储在Redis缓存中
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    /**
     * @param code
     * @param downloadFileDto
     * @return void
     * @description 保存下载码和下载文件信息
     */
    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        // 将下载码和DownloadFileDto对象存储在Redis缓存中，并设置过期时间为5分钟
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + code, downloadFileDto, Constants.REDIS_KEY_EXPIRES_FIVE_MIN);
    }

    /**
     * @param code
     * @return DownloadFileDto
     * @description 获取下载码对应的下载文件信息
     */
    public DownloadFileDto getDownloadCode(String code) {
        // 从Redis缓存中根据下载码获取相应的DownloadFileDto对象
        return (DownloadFileDto) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }

    /**
     * @param userId
     * @return UserSpaceDto
     * @description 获取用户使用的空间情况
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        // 尝试从Redis缓存中获取UserSpaceDto对象
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (null == spaceDto) {
            // 如果缓存中不存在，则创建一个新的UserSpaceDto对象，并从数据库中查询用户已使用的空间大小，并设置总空间大小为系统初始空间大小乘以常量值
            spaceDto = new UserSpaceDto();
            Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            // 将UserSpaceDto对象存储在Redis缓存中，并设置过期时间为一天
            redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }

    /**
     * @param userId
     * @param userSpaceDto
     * @return void
     * @description 保存用户使用的空间情况
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        // 创建一个新的UserSpaceDto对象，并从数据库中查询用户已使用的空间大小和用户总空间大小，并将其存储在Redis缓存中，并设置过期时间为一天
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * @param userId
     * @return UserSpaceDto
     * @description 重置用户使用的空间情况
     */
    public UserSpaceDto resetUserSpaceUse(String userId) {
        // 创建一个新的UserSpaceDto对象，并从数据库中查询用户已使用的空间大小和用户总空间大小
        UserSpaceDto spaceDto = new UserSpaceDto();
        Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        spaceDto.setUseSpace(useSpace);
        // 将其存储在Redis缓存中，并设置过期时间为一天
        UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        spaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        return spaceDto;
    }

    /**
     * @param userId
     * @param fileId
     * @param fileSize
     * @return void
     * @description 保存文件临时大小
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        // 根据用户ID和文件ID，获取当前文件的临时大小
        Long currentSize = getFileTempSize(userId, fileId);
        // 将新的文件大小加上当前大小，并将结果存储在Redis缓存中，并设置过期时间为一小时
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    /**
     * @param userId
     * @param fileId
     * @return Long
     * @description 获取文件临时大小
     */
    public Long getFileTempSize(String userId, String fileId) {
        // 根据用户ID和文件ID，从Redis缓存中获取文件的临时大小
        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        return currentSize;
    }

    /**
     * @param key
     * @return Long
     * @description 从Redis缓存中获取文件大小
     */
    private Long getFileSizeFromRedis(String key) {
        // 根据传入的键名，从Redis缓存中获取文件大小，并根据数据类型进行处理，返回文件大小
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }

        return 0L;
    }
}

