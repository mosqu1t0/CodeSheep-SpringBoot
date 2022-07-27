package com.mosquito.codesheep.service;


import cn.hutool.crypto.SecureUtil;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.utils.DealCodeResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Service
@Slf4j
public class CodeService {
    static String codeWorkPath;
    @Value("${codeWork.path}")
    public void setCodeWorkPath(String codeWorkPath) {
        CodeService.codeWorkPath = codeWorkPath;
    }

    public Map<String, Object> runCode(Code code, String sessionId){
        String id = SecureUtil.md5(sessionId);
        String Lang = code.getLang();
        String runArgs = code.getInput();
        String command = "";
        String codePath = "";
        String suffixName = "";

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

        File comPath = new File(codeWorkPath);
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
                    return DealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, null);
                }

                return DealCodeResponder.dealWrong(id, err, comCode, comRes, comErr);

            }

            //other languages run success
            if (!Lang.equals("cpp") && !Lang.equals("go")){
                return DealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, null);
            }
            //c or go compile success start to run
            if (Lang.equals("cpp")) command = "runCpp.sh";
            if (Lang.equals("go")) command = "runGo.sh";
            cmdarray = new String[]{"./" + command, runArgs, id};
            Process processSub = Runtime.getRuntime().exec(cmdarray, null, comPath);
            processSub.waitFor();

            //kill the pid, avoid something bad happen
            if (!processSub.isAlive()) processSub.destroy();

            return DealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, comExe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

