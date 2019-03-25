package com.inso.entity.http;

import java.io.Serializable;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/12
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class UserInfo implements Serializable {


    /**
     * avatar : http://106.14.205.6/storage/upload/20190201/957c3977f61047bfd3e4c4c74aae75fc_200_200.jpeg
     * gender : 男
     * birth : null
     * height : null
     * weight : null
     * signature : null
     * locale : null
     * user_id : 1086759270
     * username : 陈栋
     * nickname : null
     */

    private String avatar;
    private String gender;
    private String birth;
    private String height;
    private String weight;
    private String signature;
    private String locale;
    private String user_id;
    private String username;
    private String nickname;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", birth='" + birth + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", signature='" + signature + '\'' +
                ", locale='" + locale + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
