package com.lms.init.model.entity;


import com.lms.common.DeleteFlagEntity;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends DeleteFlagEntity {


    private String username;

    private String nickname;
    private String password;

    private String email;

    private  Integer enable;

    private String avatar;
    private  String userRole;


    private String remark;
}
