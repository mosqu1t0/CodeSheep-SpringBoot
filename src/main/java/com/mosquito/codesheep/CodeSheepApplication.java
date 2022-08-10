package com.mosquito.codesheep;

import com.mosquito.codesheep.utils.DoBeforeServeUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeSheepApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeSheepApplication.class, args);


        //autostart to copy sh to workPath
        DoBeforeServeUtil.addShToWorkPath();
    }

}
