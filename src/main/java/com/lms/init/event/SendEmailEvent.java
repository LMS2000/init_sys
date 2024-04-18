package com.lms.init.event;

import com.lms.init.model.dto.email.EmailMessage;
import com.lms.init.model.dto.email.SendEmailDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lms2000
 */
public class SendEmailEvent extends ApplicationEvent {

    @Getter
    private final EmailMessage message;
    /**
     * 设置邮件信息
     *
     * @param source
     * @param dto
     */
    public SendEmailEvent(Object source, EmailMessage dto) {
        super(source);
        this.message = dto;
    }
}
