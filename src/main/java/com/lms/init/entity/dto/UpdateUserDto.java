package com.lms.init.entity.dto;



import com.lms.init.valid.RangeCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class UpdateUserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Positive(message = "id不合法")
    @NotNull(message = "id不能为空")
    private Integer uid;


    private String nickname;
//    @NotNull(message = "账号不能为空")
//    @NotBlank(message = "账号不能为空")
//    private String password;

    @RangeCheck(range = {0,1})
    private Integer enable;


    @Email(message = "邮箱格式不正确")
    private String email;

    @Length(max = 255)
    private String remark;


}
