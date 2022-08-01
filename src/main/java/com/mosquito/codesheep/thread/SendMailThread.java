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

    private final String domain;
    private final String emailType;
    private final String mailUserName;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final String keyMsg;
    private final String emailAddress;



    @Override
    public void run() {
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMailMessage, true);
            String templateName;
            Context context = new Context();
            if (emailType.equals("activation")){
                message.setSubject("(๑•̀ㅁ•́ฅ✧ Here is Code Sheep Account Activation Mail!");
                templateName = "activate-account.html";
                String url = "http://"+domain+"/user?confirmCode=" + keyMsg;
                context.setVariable("key", url);
                context.setVariable("email", emailAddress);
            } else {
                message.setSubject("Oops, Code Sheep Error Happen!");
                templateName = "debug.html";
                context.setVariable("bugInfo", keyMsg);
            }

            message.setFrom(mailUserName);
            message.setTo(emailAddress);
            message.setSentDate(new Date());

            String text = templateEngine.process(templateName, context);
            message.setText(text, true);

            javaMailSender.send(mimeMailMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
