package com.skybox.entity.query;

import com.skybox.entity.enums.PageSize;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.query
 * @ClassName: SimplePage
 * @Datetime: 2023/11/11 19:52
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用于封装分页相关的信息
 */

public class SimplePage {
    // 当前页码，用于指定当前所在的页码
    private int pageNo;
    // 总记录数，即满足查询条件的总记录数量
    private int countTotal;
    // 每页的记录数，用于指定每页显示的记录数量
    private int pageSize;
    // 总页数，即满足查询条件的总页数
    private int pageTotal;
    // 当前页的起始记录索引
    private int start;
    // 前页的结束记录索引
    private int end;

    public SimplePage() {
    }

    public SimplePage(Integer pageNo, int countTotal, int pageSize) {
        if (null == pageNo) {
            pageNo = 0;
        }
        this.pageNo = pageNo;
        this.countTotal = countTotal;
        this.pageSize = pageSize;
        action();
    }

    public SimplePage(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @param
     * @return void
     * @description 根据当前属性的值计算和更新其他属性的值，包括总页数、起始记录索引和结束记录索引
     */
    public void action() {
        if (this.pageSize <= 0) {
            this.pageSize = PageSize.SIZE20.getSize();
        }
        if (this.countTotal > 0) {
            this.pageTotal = this.countTotal % this.pageSize == 0 ? this.countTotal / this.pageSize
                    : this.countTotal / this.pageSize + 1;
        } else {
            pageTotal = 1;
        }

        if (pageNo <= 1) {
            pageNo = 1;
        }
        if (pageNo > pageTotal) {
            pageNo = pageTotal;
        }
        this.start = (pageNo - 1) * pageSize;
        this.end = this.pageSize;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(int countTotal) {
        this.countTotal = countTotal;
        this.action();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}

