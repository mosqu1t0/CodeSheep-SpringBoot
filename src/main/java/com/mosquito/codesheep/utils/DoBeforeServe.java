package com.mosquito.codesheep.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;


@Component
@Slf4j
public class DoBeforeServe {
    static String CodeWorkPath;

    @Value("${code.workPath}")
    public void setCodeWorkPath(String codeWorkPath) {
        CodeWorkPath = codeWorkPath;
    }

    public static void addShToWorkPath(){
        File sourcePath;
        try {
            sourcePath = ResourceUtils.getFile("classpath:sh");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        File[] tempList = sourcePath.listFiles();
        File path = new File(CodeWorkPath);
        if (!path.exists()) FileUtil.mkdir(path);

        for (File file : tempList != null ? tempList : new File[0]) {
            if (file.isFile()) {
                String fileName = file.getName();
                try {
                    File sh = new File(CodeWorkPath + fileName);
                    if (sh.exists()) continue;
                    FileUtil.copyFile(file, sh);
                    if (!sh.setExecutable(true)){
                        log.error("The sh {} haven't executable permission", sh.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
