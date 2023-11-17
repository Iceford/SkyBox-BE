package com.skybox.utils;

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


@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    /**
     * @param
     * @return SysSettingsDto
     * @description 从Redis中获取系统设置，如果不存在则创建一个新的，并返回该对象
     */
    public SysSettingsDto getSysSettingsDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            saveSysSettingsDto(sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * @param sysSettingsDto
     * @return void
     * @description 将系统设置保存到Redis中
     */
    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    /**
     * @param userId
     * @param userSpaceDto
     * @return void
     * @description 保存用户已使用的空间到Redis中
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId,
                userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * @param userId
     * @return UserSpaceDto
     * @description 从Redis中获取用户已使用的空间，如果不存在则从数据库查询，并将查询结果保存到Redis中
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        if (null == spaceDto) {
            spaceDto = new UserSpaceDto();
            Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Constants.MB);
            redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }

    /**
     * @param userId
     * @param fileId
     * @param fileSize
     * @return void
     * @description 保存文件的临时大小到Redis中
     */
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId,
                currentSize + fileSize, Constants.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    /**
     * @param userId
     * @param fileId
     * @return void
     * @description 从Redis中删除文件的临时大小
     */
    public void removeFileTempSize(String userId, String fileId) {
        redisUtils.delete(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
    }

    /**
     * @param userId
     * @param fileId
     * @return Long
     * @description 从Redis中获取文件的临时大小
     */
    public Long getFileTempSize(String userId, String fileId) {
//        Long currentSize = getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
//        return currentSize;
        String key = Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId;
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

    /**
     * @param code
     * @param downloadFileDto
     * @return void
     * @description 保存下载码和下载文件的相关信息到Redis中
     */
    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + code,
                downloadFileDto, Constants.REDIS_KEY_EXPIRES_FIVE_MIN);
    }

    /**
     * @param code
     * @return DownloadFileDto
     * @description 从Redis中获取下载码对应的下载文件信息
     */
    public DownloadFileDto getDownloadCode(String code) {
        return (DownloadFileDto) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }

    /**
     * @param userId
     * @return UserSpaceDto
     * @description 重置并更新用户已使用的空间信息，并保存到Redis中
     */
    public UserSpaceDto resetUserSpaceUse(String userId) {
        UserSpaceDto spaceDto = new UserSpaceDto();
        Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
        spaceDto.setUseSpace(useSpace);

        UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        spaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        return spaceDto;
    }
}

