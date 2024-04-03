package com.lms.init.constants;

/**
 * @author LMS2000
 * @create 2023/4/22 21:22
 */
public interface FileConstant {

    /**
     * 静态资源的请求前缀
     */
    String STATIC_REQUEST_PREFIX_PATTERN="/static/**";
    String STATIC_REQUEST_PREFIX="static";

    String DEFAULT_URL="#";

    /**
     * 远程存储的桶名
     */
    String BUCKET_NAME="service-edu-2000";
    /**
     * 10M 大小
     */
    Long  ONE_M = (long) 1024 * 1024;

}
