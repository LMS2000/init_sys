package com.lms.init.entity.dto;



import com.lms.init.valid.RangeCheck;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Positive;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
@ApiModel(value="ChangeUserEnableDto", description="改变用户启用状态")
public class ChangeUserEnableDto implements Serializable {


    @Positive(message = "id不合法")
    @ApiModelProperty(value = "用户id")
    private Integer uid;
    @RangeCheck(range = {0,1})
    @ApiModelProperty(value = "用户启用状态")
    private Integer enable;
}
