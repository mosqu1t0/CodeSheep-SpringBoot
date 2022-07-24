package com.mosquito.codesheep.service;


import cn.hutool.crypto.SecureUtil;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.pojo.CodeResponder;
import com.mosquito.codesheep.tools.DeleteCodeThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
@Slf4j
public class RunCodeService {
    static String codeWorkPath;

    @Value("${codeWork.path}")
    public void setCodeWorkPath(String codeWorkPath) {
        RunCodeService.codeWorkPath = codeWorkPath;
    }

    static final String goodMsg = "Good! ฅ^•ﻌ•^ฅ";
    static final String nopMsg = "Nop （´(ｪ)｀）";
    static final String longMsg = "Long time error... (´･д･｀)";
    static final String wrongMsg = "出错啦，快联系管理员！ (´･д･｀)";
    static final int lengthOfOutput = 1024 * 1024 * 12;

    public CodeResponder compileCode(Code code, String sessionId){
        CodeResponder codeResponder = new CodeResponder();
        File comPath = new File(codeWorkPath);

        String id = SecureUtil.md5(sessionId);
        String Lang = code.getLang();
        String runArgs = code.getInput();
        String codePath = "";
        String suffixName = "";
        String command = "";

        switch (Lang) {
            case "cpp":
                codePath += "Cpp/Cpp";
                suffixName = ".cpp";
                command = "compileCpp.sh";
                break;
            case "python":
                codePath += "Py/Py";
                suffixName = ".py";
                command = "runPy.sh";
                break;
            case "javascript":
                codePath += "Js/Js";
                suffixName = ".js";
                command = "runJs.sh";
                break;
            default:
                break;
        }

        File comCode = new File(codeWorkPath + codePath + id + suffixName);
        File comInfo = new File(codeWorkPath + codePath + id + ".info");
        File comRes = new File(codeWorkPath + codePath + id + ".out");
        File comErr = new File(codeWorkPath + codePath + id + ".err");
        File comExe = new File(codeWorkPath + codePath + id);
        File comFold = comCode.getParentFile();

        if (!comFold.exists() && !comFold.mkdir()){
            log.error("Fold can't be created, please check it out.");
        }

        try {
            FileWriter writer = new FileWriter(comCode.getPath());
            writer.write(code.getCode());
            writer.flush();
            writer.close();

             // other languages support
            String[] cmdarray;
            if (!Lang.equals("cpp") &&  !Lang.equals("go")){
                // compile with run
                cmdarray = new String[]{"./" + command, runArgs, id};
            } else {
                // compile c or go
                cmdarray = new String[]{"./" + command, id};
            }

            Process process = Runtime.getRuntime().exec(cmdarray , null, comPath);
            process.waitFor(); // wait for the pid is ending, and the exitvalue will be 1

            //kill the pid, avoid something bad happen
            if (!process.isAlive()) process.destroy();


            //compile error
            if (comErr.exists() && comErr.length() > 0) {
                String err = new String(Files.readAllBytes(Paths.get(comErr.getPath())));

                //dangerous wrong
                if (err.equals("killWrong\n")){
                    dealWithResp(id, comCode, comRes, comInfo, comErr, null, codeResponder);
                    return codeResponder;
                }

                //grammar wrong
                codeResponder.setCode(244);
                codeResponder.setRes(err);
                codeResponder.setMsg(nopMsg);

                // creat a thread do delete work
                Thread thread = new Thread(new DeleteCodeThread(comCode, comRes, null, comErr, null, id));
                thread.start();

                return codeResponder;
            }

            //other languages run success
            if (!Lang.equals("cpp") && !Lang.equals("go")){
                dealWithResp(id, comCode, comRes, comInfo, comErr, null, codeResponder);
                return codeResponder;
            }

            //c or go compile success start to run
            if (Lang.equals("cpp")) command = "runCpp.sh";
            if (Lang.equals("go")) command = "runGo.sh";
            cmdarray = new String[]{"./" + command, runArgs, id};
            Process processSub = Runtime.getRuntime().exec(cmdarray, null, comPath);
            processSub.waitFor();

            //kill the pid, avoid something bad happen
            if (!processSub.isAlive()) processSub.destroy();

            dealWithResp(id, comCode, comRes, comInfo, comErr, comExe, codeResponder);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return codeResponder;
    }

    protected void dealWithResp(String id, File comCode, File comRes, File comInfo, File comErr, File comExe, CodeResponder codeResponder) throws IOException {

        String content = "";
        if (comRes.exists() && comRes.length() > 0) {
            content = new String(Files.readAllBytes(Paths.get(comRes.getPath())));
            if (content.length() > lengthOfOutput) content = content.substring(0, lengthOfOutput);
        }
        if (comInfo.exists() && comInfo.length() > 0) {
            String info = new String(Files.readAllBytes(Paths.get(comInfo.getPath())));
            content += info;

            if (comErr.exists() && comErr.length() > 0){
                codeResponder.setCode(555);
                codeResponder.setRes(content);
                codeResponder.setMsg(wrongMsg);
                //todo wrong email
                log.error("Can't kill the programm from {}", id);
                comCode = null;

            } else {
                codeResponder.setCode(233);
                codeResponder.setRes(content);
                codeResponder.setMsg(longMsg);
            }
        } else {
            codeResponder.setCode(200);
            codeResponder.setRes(content);
            codeResponder.setMsg(goodMsg);
        }

        Thread thread = new Thread(new DeleteCodeThread(comCode, comRes, comInfo, comErr, comExe, id));
        thread.start();
    }
}

