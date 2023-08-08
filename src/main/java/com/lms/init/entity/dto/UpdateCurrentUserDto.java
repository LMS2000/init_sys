package com.lms.init.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class UpdateCurrentUserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Email(message = "邮箱格式不正确")
    private String email;
    @Length(max = 20)
    private String nickname;
}
