package com.lms.init.event.listener;

import com.lms.init.client.OssClient;
import com.lms.init.config.OssProperties;
import com.lms.init.event.SendEmailEvent;
import com.lms.init.event.UploadAvatarEvent;
import com.lms.init.model.dto.file.UploadAvatarMessage;
import com.lms.init.model.entity.User;
import com.lms.init.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author lms2000
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UploadAvatarListener {

    private final UserService userService;

    private final OssClient ossClient;

    private final OssProperties ossProperties;
    /**
     * 上传头像事件处理
     * @param event
     */
    @EventListener(UploadAvatarEvent.class)
    public void onDoUploadAvatar(UploadAvatarEvent event){
        UploadAvatarMessage message = event.getMessage();
        User user = userService.getById(message.getUserId());
        // 如果用户的头像不为空，则删除原来的头像
        String avatar = user.getAvatar();
        if(!"#".equals(avatar)){
            ossClient.deleteObject(ossProperties.getBucketName(),avatar);
        }
        User et=new User();
        et.setId(user.getId());
        et.setAvatar(message.getUrl());
        userService.updateById(et);
        log.info("完成用户上传头像事件:"+user.getId());
    }


}
