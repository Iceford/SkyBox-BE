package com.skybox.entity.query;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.query
 * @ClassName: FileShareQuery
 * @Datetime: 2023/11/11 19:51
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 分享信息参数
 */

public class FileShareQuery extends BaseParam {


    /**
     * 分享ID
     */
    private String shareId;

    private String shareIdFuzzy;

    /**
     * 文件ID
     */
    private String fileId;

    private String fileIdFuzzy;

    /**
     * 用户ID
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 有效期类型 0:1天 1:7天 2:30天 3:永久有效
     */
    private Integer validType;

    /**
     * 失效时间
     */
    private String expireTime;

    private String expireTimeStart;

    private String expireTimeEnd;

    /**
     * 分享时间
     */
    private String shareTime;

    private String shareTimeStart;

    private String shareTimeEnd;

    /**
     * 提取码
     */
    private String code;

    private String codeFuzzy;

    /**
     * 浏览次数
     */
    private Integer showCount;

    private Boolean queryFileName;

    public Boolean getQueryFileName() {
        return queryFileName;
    }

    public void setQueryFileName(Boolean queryFileName) {
        this.queryFileName = queryFileName;
    }

    public String getShareId() {
        return this.shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareIdFuzzy() {
        return this.shareIdFuzzy;
    }

    public void setShareIdFuzzy(String shareIdFuzzy) {
        this.shareIdFuzzy = shareIdFuzzy;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileIdFuzzy() {
        return this.fileIdFuzzy;
    }

    public void setFileIdFuzzy(String fileIdFuzzy) {
        this.fileIdFuzzy = fileIdFuzzy;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIdFuzzy() {
        return this.userIdFuzzy;
    }

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public Integer getValidType() {
        return this.validType;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public String getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getExpireTimeStart() {
        return this.expireTimeStart;
    }

    public void setExpireTimeStart(String expireTimeStart) {
        this.expireTimeStart = expireTimeStart;
    }

    public String getExpireTimeEnd() {
        return this.expireTimeEnd;
    }

    public void setExpireTimeEnd(String expireTimeEnd) {
        this.expireTimeEnd = expireTimeEnd;
    }

    public String getShareTime() {
        return this.shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public String getShareTimeStart() {
        return this.shareTimeStart;
    }

    public void setShareTimeStart(String shareTimeStart) {
        this.shareTimeStart = shareTimeStart;
    }

    public String getShareTimeEnd() {
        return this.shareTimeEnd;
    }

    public void setShareTimeEnd(String shareTimeEnd) {
        this.shareTimeEnd = shareTimeEnd;
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

    public Integer getShowCount() {
        return this.showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

}

