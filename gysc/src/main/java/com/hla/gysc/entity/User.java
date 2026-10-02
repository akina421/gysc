package com.hla.gysc.entity;

import lombok.Data;

/**
 * 用户
 */
@Data
public class User {
    private Integer user_id;
    private String user_name;
    private String user_nickname;
    private String user_password;
    private String user_realname;
    private Byte user_gender;
    private String user_birthday;
    private String user_profile_picture_src;
    private Integer del_flag;
}
