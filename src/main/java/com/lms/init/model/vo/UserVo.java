package com.lms.init.model.vo;

import com.lms.common.BaseVO;
import com.lms.common.DeleteFlagEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lms2000
 */
@NoArgsConstructor
@Data
@Slf4j
public class UserVo extends BaseVO {


    private String avatar;
    private String username;
    private String email;
    private String nickname;

    private String userRole;

    private  Integer enable;


}
