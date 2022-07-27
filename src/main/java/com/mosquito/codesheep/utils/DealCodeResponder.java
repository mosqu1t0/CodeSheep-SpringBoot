package com.mosquito.codesheep.utils;

import com.mosquito.codesheep.thread.DeleteCodeThread;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DealCodeResponder {

    static final String goodMsg = "Good! ฅ^•ﻌ•^ฅ";
    static final String nopMsg = "Nop （´(ｪ)｀）";
    static final String longMsg = "Long time error... (´･д･｀)";
    static final String wrongMsg = "出错啦，快联系管理员！ (´･д･｀)";
    static final int lengthOfOutput = 1024 * 1024 * 12;



    public static Map<String, Object> dealWrong(String id, String err, File comCode, File comRes, File comErr){
        //grammar wrong
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("code", 244);
        resMap.put("res", err);
        resMap.put("msg", nopMsg);
        // creat a thread do delete work
        Thread thread = new Thread(new DeleteCodeThread(comCode, comRes, null, comErr, null, id));
        thread.start();

        return resMap;
    }
    public static Map<String, Object> dealRight(String id, File comCode, File comRes, File comInfo, File comErr, File comExe) throws IOException {
        Map<String, Object> resMap = new HashMap<>();

        String content = "";
        if (comRes.exists() && comRes.length() > 0) {
            content = new String(Files.readAllBytes(Paths.get(comRes.getPath())));
            if (content.length() > lengthOfOutput) content = content.substring(0, lengthOfOutput);
        }
        if (comInfo.exists() && comInfo.length() > 0) {
            String info = new String(Files.readAllBytes(Paths.get(comInfo.getPath())));
            content += info;

            if (comErr.exists() && comErr.length() > 0){
                resMap.put("code", 555);
                resMap.put("res", content);
                resMap.put("msg", wrongMsg);
                comCode = null;
                //todo wrong email
                log.error("Can't kill the programm from {}", id);

            } else {
                resMap.put("code", 233);
                resMap.put("res", content);
                resMap.put("msg", longMsg);
            }
        } else {
            resMap.put("code", 200);
            resMap.put("res", content);
            resMap.put("msg", goodMsg);
        }

        Thread thread = new Thread(new DeleteCodeThread(comCode, comRes, comInfo, comErr, comExe, id));
        thread.start();
        return resMap;
    }
}
