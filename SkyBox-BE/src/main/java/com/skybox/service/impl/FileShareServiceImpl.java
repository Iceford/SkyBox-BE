package com.skybox.service.impl;

import com.skybox.entity.constants.Constants;
import com.skybox.entity.dto.SessionShareDto;
import com.skybox.entity.enums.PageSize;
import com.skybox.entity.enums.ResponseCodeEnum;
import com.skybox.entity.enums.ShareValidTypeEnums;
import com.skybox.entity.po.FileShare;
import com.skybox.entity.query.FileShareQuery;
import com.skybox.entity.query.SimplePage;
import com.skybox.entity.vo.PaginationResultVO;
import com.skybox.exception.BusinessException;
import com.skybox.mappers.FileShareMapper;
import com.skybox.service.FileShareService;
import com.skybox.utils.DateUtil;
import com.skybox.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.service.impl
 * @ClassName: FileShareServiceImpl
 * @Datetime: 2023/11/11 21:17
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 处理文件分享相关的业务逻辑
 */

@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * @param param
     * @return List<FileShare>
     * @description 根据参数条件查询文件分享列表
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery param) {
        return this.fileShareMapper.selectList(param);
    }

    /**
     * @param param
     * @return Integer
     * @description 根据参数条件查询文件分享数量
     */
    @Override
    public Integer findCountByParam(FileShareQuery param) {
        return this.fileShareMapper.selectCount(param);
    }

    /**
     * @param param
     * @return PaginationResultVO<FileShare>
     * @description 分页查询文件分享列表
     */
    @Override
    public PaginationResultVO<FileShare> findListByPage(FileShareQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileShare> list = this.findListByParam(param);
        PaginationResultVO<FileShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * @param bean
     * @return Integer
     * @description 新增文件分享
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * @param listBean
     * @return Integer
     * @description 批量新增文件分享
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * @param listBean
     * @return Integer
     * @description 批量新增或修改文件分享
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * @param shareId
     * @return FileShare
     * @description 根据分享ID获取文件分享对象
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * @param bean
     * @param shareId
     * @return Integer
     * @description 根据分享ID修改文件分享对象
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * @param shareId
     * @return Integer
     * @description 根据分享ID删除文件分享对象
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    /**
     * @param shareIdArray
     * @param userId
     * @return void
     * @description 批量删除文件分享对象
     */
    @Override
    @Transactional
    public void deleteFileShareBatch(String[] shareIdArray, String userId) {
        Integer count = fileShareMapper.deleteFileShareBatch(shareIdArray, userId);
        if (count != shareIdArray.length) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * @param share
     * @return void
     * @description 保存文件分享信息
     */
    @Override
    public void saveShare(FileShare share) {
        ShareValidTypeEnums typeEnum = ShareValidTypeEnums.getByType(share.getValidType());
        if (null == typeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 设置过期时间
        if (typeEnum != ShareValidTypeEnums.FOREVER) {
            share.setExpireTime(DateUtil.getAfterDate(typeEnum.getDays()));
        }
        Date curDate = new Date();
        // 设置分享时间
        share.setShareTime(curDate);
        if (StringTools.isEmpty(share.getCode())) {
            share.setCode(StringTools.getRandomString(Constants.LENGTH_5));
        }
        share.setShareId(StringTools.getRandomString(Constants.LENGTH_20));
        this.fileShareMapper.insert(share);
    }

    /**
     * @param shareId
     * @param code
     * @return SessionShareDto
     * @description 验证分享提取码是否正确，并返回相关的分享信息
     */
    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare share = this.fileShareMapper.selectByShareId(shareId);
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        if (!share.getCode().equals(code)) {
            throw new BusinessException("提取码错误");
        }

        //更新浏览次数
        this.fileShareMapper.updateShareShowCount(shareId);

        SessionShareDto shareSessionDto = new SessionShareDto();
        shareSessionDto.setShareId(shareId);
        shareSessionDto.setShareUserId(share.getUserId());
        shareSessionDto.setFileId(share.getFileId());
        shareSessionDto.setExpireTime(share.getExpireTime());
        return shareSessionDto;
    }

}

