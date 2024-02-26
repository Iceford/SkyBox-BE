package com.skybox.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.mappers
 * @ClassName: BaseMapper
 * @Datetime: 2023/11/11 20:46
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于定义基本的数据库操作方法
 */

interface BaseMapper<T, P> {
    /**
     * insert:(插入)
     */
    Integer insert(@Param("bean") T t);

    /**
     * insertOrUpdate:(插入或者更新)
     */
    Integer insertOrUpdate(@Param("bean") T t);

    /**
     * insertBatch:(批量插入)
     */
    Integer insertBatch(@Param("list") List<T> list);

    /**
     * insertOrUpdateBatch:(批量插入或更新)
     */
    Integer insertOrUpdateBatch(@Param("list") List<T> list);

    /**
     * selectList:(根据参数查询集合)
     */
    List<T> selectList(@Param("query") P p);

    /**
     * selectCount:(根据集合查询数量)
     */
    Integer selectCount(@Param("query") P p);
}

