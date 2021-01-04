package com.wymx.springboot;

import com.wymx.springboot.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("3507222986@qq.com", "12345", "ok");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username" , "任静");
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("3507222986@qq.com", "html", process);
    }

}
