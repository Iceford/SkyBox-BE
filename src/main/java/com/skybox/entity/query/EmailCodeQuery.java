package com.skybox.entity.query;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.query
 * @ClassName: EmailCodeQuery
 * @Datetime: 2023/11/11 19:50
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 邮箱验证码参数
 */

public class EmailCodeQuery extends BaseParam {


    // 邮箱
    private String email;

    private String emailFuzzy;

    // 编号
    private String code;

    private String codeFuzzy;

    // 创建时间
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    // 状态: 0-未使用 1-已使用
    private Integer status;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailFuzzy() {
        return this.emailFuzzy;
    }

    public void setEmailFuzzy(String emailFuzzy) {
        this.emailFuzzy = emailFuzzy;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeFuzzy() {
        return this.codeFuzzy;
    }

    public void setCodeFuzzy(String codeFuzzy) {
        this.codeFuzzy = codeFuzzy;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStart() {
        return this.createTimeStart;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeEnd() {
        return this.createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

