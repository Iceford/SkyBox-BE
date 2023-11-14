package com.skybox.entity.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.vo
 * @ClassName: PaginationResultVO
 * @Datetime: 2023/11/11 19:34
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 表示分页查询的结果
 */

public class PaginationResultVO<T> {
    // 总记录数，即满足查询条件的记录的总数
    private Integer totalCount;
    // 每页显示的记录数
    private Integer pageSize;
    // 当前页码
    private Integer pageNo;
    // 总页数
    private Integer pageTotal;
    // 当前页的数据列表
    private List<T> list = new ArrayList<T>();

    public PaginationResultVO(Integer totalCount, Integer pageSize, Integer pageNo, List<T> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.list = list;
    }

    public PaginationResultVO(Integer totalCount, Integer pageSize, Integer pageNo, Integer pageTotal, List<T> list) {
        if (pageNo == 0) {
            pageNo = 1;
        }
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.pageTotal = pageTotal;
        this.list = list;
    }

    public PaginationResultVO(List<T> list) {
        this.list = list;
    }

    public PaginationResultVO() {

    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }
}
