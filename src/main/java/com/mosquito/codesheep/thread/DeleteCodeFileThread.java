package com.mosquito.codesheep.thread;

import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.utils.languageMapUtil;
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
        String suffix = languageMapUtil.getSuffix(code.getLanguage());

        File file = new File(savePath + email + '/' + code.getFileName() + suffix);

        if (file.exists() && !file.delete()){
            log.error("can't delete saveCode {}", file.getPath());
        }
    }
}
