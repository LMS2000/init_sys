package com.lms.init.exception;


import cn.dev33.satoken.exception.NotLoginException;
import com.lms.contants.HttpCode;
import com.lms.exception.GlobalExceptionHandler;
import com.lms.result.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.lms.contants.HttpCode.NO_AUTH_ERROR;

@RestControllerAdvice
@Slf4j
public class InitGlobalExceptionHandler extends GlobalExceptionHandler {




    @ExceptionHandler(NotLoginException.class)
    public ResultData NotLoginException(NotLoginException notLoginException){
        log.error("进入自定义异常："+notLoginException.getCode());
        return ResultData.error(HttpCode.NOT_LOGIN_ERROR,notLoginException.getMessage(),notLoginException);
    }
    /**
     * 控制返回的状态码
     * @param businessException
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResultData AuthException(BusinessException businessException){
        log.error("进入自定义异常："+businessException.getCode());

        HttpCode[] values = HttpCode.values();
        for (HttpCode httpCode : values) {
            if(httpCode.getCode()==businessException.getCode()){
                return ResultData.error(httpCode,
                        businessException.getMessage(),null);
            }
        }
        return ResultData.error(businessException.getMessage());
    }


}
