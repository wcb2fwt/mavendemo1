package com.wymx.springboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Component
public class MailClient {

    //声明log记录日志
    private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

    //注入核心组件，JavaMailSender是由spring管理的
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 我们发邮件的时候需要几个条件。
     * 第一个是 我的邮箱是什么？（发送人）
     * 第二个是 接收邮件的人是谁？（接收人）
     * 第三个是 我发送的邮件标题和内容是什么？
     *
     * 因为每次发送邮件的发送人是固定的，所以我们直接将发送人注入到bean当中来，而不用每次都传进来
     */

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        /**
         * to:接收人  subject:邮件的标题  content:邮件的内容
         */
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content,true);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            LOGGER.error("发送邮件失败："+e.getMessage());
        }

    }

}
