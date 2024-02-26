package com.skybox.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.mappers
 * @ClassName: FileShareMapper
 * @Datetime: 2023/11/11 20:51
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 针对分享信息的数据库操作接口
 */

public interface FileShareMapper<T, P> extends BaseMapper<T, P> {

    /**
     * @param t       要更新的对象
     * @param shareId 分享ID
     * @return Integer 一个整数，表示更新的记录数
     * @description 根据分享ID更新数据库记录
     */
    Integer updateByShareId(@Param("bean") T t, @Param("shareId") String shareId);

    /**
     * @param shareId 分享ID
     * @return Integer 一个整数，表示删除的记录数
     * @description 根据分享ID删除数据库记录
     */
    Integer deleteByShareId(@Param("shareId") String shareId);

    /**
     * @param shareId 分享ID
     * @return T 一个泛型类型 T 的对象
     * @description 据分享ID查询数据库记录并返回对象
     */
    T selectByShareId(@Param("shareId") String shareId);

    /**
     * @param shareIdArray 分享ID数组
     * @param userId       用户ID
     * @return Integer 一个整数，表示删除的记录数
     * @description 批量删除文件分享记录
     */
    Integer deleteFileShareBatch(@Param("shareIdArray") String[] shareIdArray, @Param("userId") String userId);

    /**
     * @param shareId 分享ID
     * @return void
     * @description 更新分享的展示次数
     */
    void updateShareShowCount(@Param("shareId") String shareId);
}

