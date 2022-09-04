package com.mosquito.codesheep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodeSheepApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeSheepApplication.class, args);
        //autostart to copy sh to workPath
//        DoBeforeServeUtil.addShToWorkPath();
    }

}
