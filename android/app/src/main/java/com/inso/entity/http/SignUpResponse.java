package com.inso.entity.http;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/12
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class SignUpResponse {

    /**
     * user_id : 1086759270
     * nickname : null
     * username : 陈栋
     * phone : null
     * access_token : VyU7MmlnEH-In4YpCOiFBzwNfIVA5c4f
     * expired_at : 1551601640
     * profile : {"avatar":"http://106.14.205.6/storage/upload/20190201/957c3977f61047bfd3e4c4c74aae75fc_200_200.jpeg","gender":"男","birth":null,"height":null,"weight":null,"signature":null,"locale":null}
     */

    private String user_id;
    private Object nickname;
    private String username;
    private Object phone;
    private String access_token;
    private String expired_at;
    private ProfileBean profile;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Object getNickname() {
        return nickname;
    }

    public void setNickname(Object nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(String expired_at) {
        this.expired_at = expired_at;
    }

    public ProfileBean getProfile() {
        return profile;
    }

    public void setProfile(ProfileBean profile) {
        this.profile = profile;
    }

    public static class ProfileBean {
        /**
         * avatar : http://106.14.205.6/storage/upload/20190201/957c3977f61047bfd3e4c4c74aae75fc_200_200.jpeg
         * gender : 男
         * birth : null
         * height : null
         * weight : null
         * signature : null
         * locale : null
         */

        private String avatar;
        private String gender;
        private Object birth;
        private Object height;
        private Object weight;
        private Object signature;
        private Object locale;

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

        public Object getBirth() {
            return birth;
        }

        public void setBirth(Object birth) {
            this.birth = birth;
        }

        public Object getHeight() {
            return height;
        }

        public void setHeight(Object height) {
            this.height = height;
        }

        public Object getWeight() {
            return weight;
        }

        public void setWeight(Object weight) {
            this.weight = weight;
        }

        public Object getSignature() {
            return signature;
        }

        public void setSignature(Object signature) {
            this.signature = signature;
        }

        public Object getLocale() {
            return locale;
        }

        public void setLocale(Object locale) {
            this.locale = locale;
        }
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "user_id='" + user_id + '\'' +
                ", nickname=" + nickname +
                ", username='" + username + '\'' +
                ", phone=" + phone +
                ", access_token='" + access_token + '\'' +
                ", expired_at='" + expired_at + '\'' +
                ", profile=" + profile +
                '}';
    }
}
