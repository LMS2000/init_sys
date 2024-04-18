package com.lms.init.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.lms.common.DeleteFlagEntity;
import lombok.*;


/**
 * @author lms2000
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class User extends DeleteFlagEntity {


    private String username;

    private String nickname;
    private String password;

    private String email;

    @TableField("is_enable")
    private  Integer enable;

    private String avatar;
    private  String userRole;


    private String remark;
}
