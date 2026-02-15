package com.minjor.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minjor.data.entity.BaseEntity;
import com.minjor.data.enums.DataStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@TableName(User.TABLE_NAME)
@NoArgsConstructor
public class User extends BaseEntity {
    public static final String TABLE_NAME = "auth_users";

    private String username;
    
    private String password;
    
    private String email;
    
    private String fullName;

    private DataStatus status;

    private List<Role> roles;

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

}
