package com.inso.entity.http.post;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Sign {
   public String  platform	;
   public String  nickname	;
   public String  unionId	;
   public String  avatar	;
   public String  gender	;

    public Sign(String arg_platform, String arg_nickname, String arg_unionId, String arg_avatar, String arg_gender) {
        platform = arg_platform;
        nickname = arg_nickname;
        unionId = arg_unionId;
        avatar = arg_avatar;
        gender = arg_gender;
    }
}
