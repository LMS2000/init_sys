package com.lms.init.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.lms.contants.HttpCode;
import com.lms.exception.BusinessException;
import com.lms.init.client.OssClient;
import com.lms.init.constants.FileConstant;
import com.lms.init.model.dto.file.UploadFileRequest;
import com.lms.init.model.enums.FileUploadBizEnum;
import com.lms.result.EnableResponseAdvice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * 上传文件
 */
@RequestMapping("/file")
@Slf4j
@AllArgsConstructor
@RestController
@Api(value = "文件管理")
@EnableResponseAdvice
public class FileController {



    private final OssClient ossClient;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @return
     */
    @PostMapping("/upload")
    @SaCheckLogin
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "上传文件")
    public String uploadFile(@RequestPart("file") MultipartFile multipartFile,
                             UploadFileRequest uploadFileRequest) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(HttpCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        Long loginId = Long.parseLong((String) StpUtil.getLoginId());


        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginId, filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);

            ossClient.putObject(FileConstant.BUCKET_NAME, filepath, new FileInputStream(file));
            // 返回可访问地址
            return filepath;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(HttpCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > FileConstant.ONE_M) {
                throw new BusinessException(HttpCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(HttpCode.PARAMS_ERROR, "文件类型错误");
            }
        }else{
            if(!"zip".equals(fileSuffix)){
                throw new BusinessException(HttpCode.PARAMS_ERROR,"仅支持zip压缩文件");
            }
        }
    }
}
