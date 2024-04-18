package com.lms.init.event.listener;

import com.lms.contants.HttpCode;
import com.lms.init.constants.CommonConstant;
import com.lms.init.constants.EmailConstant;
import com.lms.init.event.SendEmailEvent;
import com.lms.init.exception.BusinessException;
import com.lms.init.model.dto.email.EmailMessage;
import com.lms.init.model.dto.email.SendEmailDto;
import com.lms.init.utils.StringTools;
import com.lms.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *  发送邮件监听器
 * @author lms2000
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SendEmailListener {

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送人
     */
    @Value("${spring.mail.username}")
    private String sendUserName;
    @Resource
    private RedisCache redisCache;
    /**
     * 处理发送邮件
     * @param event
     */
    @EventListener(SendEmailEvent.class)
    public void onSendEmail(SendEmailEvent event){
        EmailMessage message = event.getMessage();
        Integer type = message.getMsgType();
        String email = message.getEmail();
        String code = StringTools.getRandomNumber(CommonConstant.LENGTH_5);
        //设置15分钟的失效时间
        redisCache.setCacheObject(EmailConstant.EMAIIL_HEADER + type + "_" + email, code, 15, TimeUnit.MINUTES);
        sendEmailCode(message,code);
    }
    private void sendEmailCode(EmailMessage msg, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //邮件发件人
            helper.setFrom(sendUserName);
            //邮件收件人 1或多个
            helper.setTo(msg.getEmail());
            //邮件主题
            helper.setSubject(msg.getRegisterEmailTitle());
            //邮件内容
            helper.setText(String.format(msg.getRegisterEmailContent(), code));
            //邮件发送时间
            helper.setSentDate(new Date());
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BusinessException(HttpCode.OPERATION_ERROR,"邮件发送失败");
        }
    }
}
