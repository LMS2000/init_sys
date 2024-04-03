package com.lms.init.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * @author lms2000
 */
@Configuration
public class FileLImitConfig {
    @Value("${spring.http.server.maxFileSize}")
    private Long maxFileSize;
    @Value("${spring.http.server.maxRequestSize}")
    private Long maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个数据大小
        // factory.setMaxFileSize(MaxFileSize); // KB,MB
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));
        // factory.setMaxRequestSize(MaxRequestSize);
        return factory.createMultipartConfig();
    }
}
