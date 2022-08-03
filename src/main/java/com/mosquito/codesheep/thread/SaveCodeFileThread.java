package com.mosquito.codesheep.thread;

import cn.hutool.core.io.file.FileWriter;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.utils.languageMapUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


@AllArgsConstructor
@Slf4j
public class SaveCodeFileThread implements Runnable{
    private Code code;
    private String email;
    private String savePath;

    @Override
    public void run() {
        String suffix = languageMapUtil.getSuffix(code.getLanguage());
        File path = new File(savePath + email + '/');
        if (!path.exists() && !path.mkdir()) {
            log.error("can't creat saveCode path.");
        }

        FileWriter fileWriter = new FileWriter(path.getPath()+ '/' + code.getFileName() + suffix);
        fileWriter.write(code.getCode());
    }
}
