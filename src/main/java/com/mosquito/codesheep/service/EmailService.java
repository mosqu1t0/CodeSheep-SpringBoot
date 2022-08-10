package com.mosquito.codesheep.service;

import com.mosquito.codesheep.thread.SendMailThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.annotation.Resource;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${bugEmail.receiver}")
    private String bugEmail;
    @Value("${doMain}")
    private String domain;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private TemplateEngine templateEngine;

    public void sendMailForActivationAccount(String activationUrl, String email){
        threadPoolTaskExecutor.execute(new SendMailThread(domain, "activation", emailFrom, javaMailSender, templateEngine, activationUrl, email));
    }

    public void sendMailForBugs(String info){
        threadPoolTaskExecutor.execute(new SendMailThread(domain, "bugs", emailFrom, javaMailSender, templateEngine, info, bugEmail));
    }

}
