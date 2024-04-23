package com.lms.init.model.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 * @author lms2000
 * @since 2024-02-01
 */
@Data
@AllArgsConstructor
@ApiModel(value = "UploadAvatarMessage对象", description = "文件上传")
public class UploadAvatarMessage implements Serializable {

    /**
     * 业务
     */
    private String url;
    /**
     *
     * 用户id
     */
    private Long userId;

}