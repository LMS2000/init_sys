package com.lms.init.client;

import org.springframework.http.MediaTypeFactory;

//获取文件的contentType
public class ContentNegotiateManager {
    public static String getMimeType(String objectName) {
        try{
            return MediaTypeFactory.getMediaType(objectName).get().toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "application/octet-stream";
    }
}
