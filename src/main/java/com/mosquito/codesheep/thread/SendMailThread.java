package com.mosquito.codesheep.thread;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@AllArgsConstructor
public class SendMailThread implements Runnable{

    private String emailType;
    private String mailUserName;
    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    private String keyMsg;
    private String emailAddress;


    @Override
    public void run() {
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMailMessage, true);
            String templateName;
            if (emailType.equals("activation")){
                message.setSubject("(๑•̀ㅁ•́ฅ✧ Here is Code Sheep Account Activation Mail!");
                templateName = "activate-account.html";
            } else {
                message.setSubject("Oops, Code Sheep Error Happen!");
                templateName = "debug.html";
            }

            message.setFrom(mailUserName);
            message.setTo(emailAddress);
            message.setSentDate(new Date());

            Context context = new Context();
            String url = "http://localhost:8080/user?confirmCode=" + keyMsg;
            context.setVariable("key", url);
            String text = templateEngine.process(templateName, context);
            message.setText(text, true);

            javaMailSender.send(mimeMailMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
