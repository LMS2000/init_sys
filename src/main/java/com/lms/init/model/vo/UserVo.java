package com.lms.init.model.vo;

import com.lms.common.DeleteFlagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lms2000
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class UserVo extends DeleteFlagEntity {


    private String avatar;
    private String username;
    private String email;
    private String nickname;

    private String userRole;

    private  Integer enable;


}
