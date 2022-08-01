package com.mosquito.codesheep.thread;

import com.mosquito.codesheep.pojo.Code;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@AllArgsConstructor
@Slf4j
public class DeleteCodeFileThread implements Runnable {
    private Code code;
    private String email;
    private String savePath;
    @Override
    public void run() {
        String suffix = "";
        switch (code.getLanguage()){
            case "cpp":
                suffix = ".cpp";
                break;
            case "go":
                suffix = ".go";
                break;
            case "python":
                suffix = ".py";
                break;
            case "javascript":
                suffix = ".js";
                break;
        }

        File file = new File(savePath + email + '/' + code.getFileName() + suffix);

        if (file.exists() && !file.delete()){
            log.error("can't delete saveCode {}", file.getPath());
        }
    }
}
