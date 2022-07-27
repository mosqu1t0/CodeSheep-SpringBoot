package com.mosquito.codesheep.service;

import com.mosquito.codesheep.thread.SendMailThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.annotation.Resource;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private TemplateEngine templateEngine;

    public void sendMailForActivationAccount(String activationUrl, String email){
        Thread thread = new Thread(new SendMailThread("activation", emailFrom, javaMailSender, templateEngine, activationUrl, email));
        thread.start();
    }

}
