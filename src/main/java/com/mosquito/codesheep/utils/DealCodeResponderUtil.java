package com.mosquito.codesheep.utils;

import com.mosquito.codesheep.service.EmailService;
import com.mosquito.codesheep.thread.CleanCodeThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DealCodeResponderUtil {

    static final String goodMsg = "Good! ฅ^•ﻌ•^ฅ";
    static final String nopMsg = "Nop （´(ｪ)｀）";
    static final String longMsg = "Long time error... (´･д･｀)";
    static final String wrongMsg = "出错啦，快联系管理员！ (´･д･｀)";
    static final int lengthOfOutput = 1024 * 1024 * 12;

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
            content = new String(Files.readAllBytes(Paths.get(comOut.getPath())));
            if (content.length() > lengthOfOutput) content = content.substring(0, lengthOfOutput);
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
