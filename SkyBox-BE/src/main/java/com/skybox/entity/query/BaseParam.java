package com.skybox.entity.query;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.query
 * @ClassName: BaseParam
 * @Datetime: 2023/11/11 19:48
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于传递基本的查询参数
 */

public class BaseParam {
    // 一个简单的分页对象，用于包含分页相关的信息
    private SimplePage simplePage;
    // 当前页码
    private Integer pageNo;
    // 每页的记录数
    private Integer pageSize;
    // 排序方式
    private String orderBy;

    public SimplePage getSimplePage() {
        return simplePage;
    }

    public void setSimplePage(SimplePage simplePage) {
        this.simplePage = simplePage;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}

