package com.skybox.entity.query;

/**
 * @Project: SkyBox-BE
 * @Package: com.skybox.entity.query
 * @ClassName: UserInfoQuery
 * @Datetime: 2023/11/11 19:55
 * @Author: HuangRongQuan
 * @Email: rongquanhuang01@gmail.com
 * @Description: 用户信息参数
 */

public class UserInfoQuery extends BaseParam {


    // 用户ID
    private String userId;
    private String userIdFuzzy;

    // 昵称
    private String nickName;

    private String nickNameFuzzy;

    // 邮箱
    private String email;
    private String emailFuzzy;

    // qq头像
    private String qqAvatar;
    private String qqAvatarFuzzy;

    // qqOpenId
    private String qqOpenId;
    private String qqOpenIdFuzzy;

    // 密码
    private String password;
    private String passwordFuzzy;

    // 加入时间
    private String joinTime;
    private String joinTimeStart;

    private String joinTimeEnd;

    // 最后登录时间
    private String lastLoginTime;
    private String lastLoginTimeStart;

    private String lastLoginTimeEnd;

    // 0-禁用 1-正常
    private Integer status;

    // 使用空间
    private Long useSpace;

    // 总空间
    private Long totalSpace;

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

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickNameFuzzy() {
        return this.nickNameFuzzy;
    }

    public void setNickNameFuzzy(String nickNameFuzzy) {
        this.nickNameFuzzy = nickNameFuzzy;
    }

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

    public String getQqAvatar() {
        return this.qqAvatar;
    }

    public void setQqAvatar(String qqAvatar) {
        this.qqAvatar = qqAvatar;
    }

    public String getQqAvatarFuzzy() {
        return this.qqAvatarFuzzy;
    }

    public void setQqAvatarFuzzy(String qqAvatarFuzzy) {
        this.qqAvatarFuzzy = qqAvatarFuzzy;
    }

    public String getQqOpenId() {
        return this.qqOpenId;
    }

    public void setQqOpenId(String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }

    public String getQqOpenIdFuzzy() {
        return this.qqOpenIdFuzzy;
    }

    public void setQqOpenIdFuzzy(String qqOpenIdFuzzy) {
        this.qqOpenIdFuzzy = qqOpenIdFuzzy;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordFuzzy() {
        return this.passwordFuzzy;
    }

    public void setPasswordFuzzy(String passwordFuzzy) {
        this.passwordFuzzy = passwordFuzzy;
    }

    public String getJoinTime() {
        return this.joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getJoinTimeStart() {
        return this.joinTimeStart;
    }

    public void setJoinTimeStart(String joinTimeStart) {
        this.joinTimeStart = joinTimeStart;
    }

    public String getJoinTimeEnd() {
        return this.joinTimeEnd;
    }

    public void setJoinTimeEnd(String joinTimeEnd) {
        this.joinTimeEnd = joinTimeEnd;
    }

    public String getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginTimeStart() {
        return this.lastLoginTimeStart;
    }

    public void setLastLoginTimeStart(String lastLoginTimeStart) {
        this.lastLoginTimeStart = lastLoginTimeStart;
    }

    public String getLastLoginTimeEnd() {
        return this.lastLoginTimeEnd;
    }

    public void setLastLoginTimeEnd(String lastLoginTimeEnd) {
        this.lastLoginTimeEnd = lastLoginTimeEnd;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUseSpace() {
        return this.useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return this.totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

}

