package com.mosquito.codesheep.utils;

import com.mosquito.codesheep.service.EmailService;
import com.mosquito.codesheep.thread.CleanCodeThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DealCodeResponderUtil {

    final String goodMsg = "Good! ฅ^•ﻌ•^ฅ";
    final String nopMsg = "Nop （´(ｪ)｀）";
    final String longMsg = "Long time error... (´･д･｀)";
    final String wrongMsg = "出错啦，快联系管理员！ (´･д･｀)";

    @Value("${code.maxReadByte}")
    private int lengthOfOutput;
    @Resource
    EmailService emailService;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public Map<String, Object> dealWrong(String id, String err, File comCode, File comOut, File comErr){
        //grammar wrong
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 244);
        resultMap.put("res", err);
        resultMap.put("msg", nopMsg);
        // creat a thread do delete work

        threadPoolTaskExecutor.execute(new CleanCodeThread(comCode, comOut, null, comErr, null, id));

        return resultMap;
    }
    public Map<String, Object> dealRight(String id, File comCode, File comOut, File comInfo, File comErr, File comExe) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();

        String content = "";
        if (comOut.exists() && comOut.length() > 0) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(comOut));
            int lenReal = (int) comOut.length();
            char[] buf;
            int read;
            if (lenReal < lengthOfOutput) {
                buf = new char[lenReal];
                read = bufferedReader.read(buf, 0, lenReal);
            } else {
                buf = new char[lengthOfOutput];
                read = bufferedReader.read(buf, 0, lengthOfOutput);
            }
            if (read != 0) {
                content = String.valueOf(buf).replaceAll(" ", ""); // remove null char
            }
            bufferedReader.close();
        }
        if (comInfo.exists() && comInfo.length() > 0) {
            String info = new String(Files.readAllBytes(Paths.get(comInfo.getPath())));
            content += info;
            if (comErr.exists() && comErr.length() > 0){
                resultMap.put("code", 555);
                resultMap.put("res", content);
                resultMap.put("msg", wrongMsg);
                //keep wrong code
                comCode = null;

                log.error("Can't kill the programm from {}", id);
                String bugInfo = "无法关闭程序: " + id;
                emailService.sendMailForBugs(bugInfo);

            } else {
                resultMap.put("code", 233);
                resultMap.put("res", content);
                resultMap.put("msg", longMsg);
            }
        } else {
            resultMap.put("code", 200);
            resultMap.put("res", content);
            resultMap.put("msg", goodMsg);
        }

        threadPoolTaskExecutor.execute(new CleanCodeThread(comCode, comOut, comInfo, comErr, comExe, id));
        return resultMap;
    }
}
