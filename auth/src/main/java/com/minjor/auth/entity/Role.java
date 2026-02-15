package com.minjor.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minjor.data.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@TableName("auth_roles")
@NoArgsConstructor
public class Role extends BaseEntity {
    private String name;
    
    private String description;

    private List<Role> roles;
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
