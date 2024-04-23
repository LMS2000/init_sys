package com.lms.init.event;

import com.lms.init.model.dto.file.UploadAvatarMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author lms2000
 */
public class UploadAvatarEvent extends ApplicationEvent {
    @Getter
    private final UploadAvatarMessage message;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param message
     */
    public UploadAvatarEvent(Object source, UploadAvatarMessage message) {
        super(source);
        this.message = message;
    }
}
