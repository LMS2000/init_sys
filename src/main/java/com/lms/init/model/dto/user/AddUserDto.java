package com.lms.init.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
@ApiModel(value="AddUserDto", description="添加用户")
public class AddUserDto implements Serializable {
    private static final long serialVersionUID = 1L;


    @NotBlank(message = "用户名不能为空")
    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码")
    private String password;

    @NotBlank(message = "用户角色不能为空")
    @NotNull(message = "用户角色不能为空")
    @ApiModelProperty(value = "用户角色")
    private String userRole;


    @Length(max = 255)
    @ApiModelProperty(value = "备注")
    private String remark;




}
