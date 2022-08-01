package com.mosquito.codesheep.service;


import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.Page;
import com.mosquito.codesheep.mapper.CodeMapper;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.thread.DeleteCodeFileThread;
import com.mosquito.codesheep.thread.SaveCodeFileThread;
import com.mosquito.codesheep.utils.DealCodeResponder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class CodeService {

    @Resource
    DealCodeResponder dealCodeResponder;
    @Resource
    CodeMapper codeMapper;
    static String codeWorkPath;
    static String codeSavePath;
    @Value("${code.workPath}")
    public void setCodeWorkPath(String codeWorkPath) {
        CodeService.codeWorkPath = codeWorkPath;
    }
    @Value("${code.savePath}")
    public void setCodeSavePath(String codeSavePath) {
        CodeService.codeSavePath = codeSavePath;
    }

    public Map<String, Object> runCode(Code code, String sessionId){
        String id = SecureUtil.md5(sessionId);
        String Lang = code.getLanguage();
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
            FileWriter fileWriter = new FileWriter(comCode.getPath());
            fileWriter.write(code.getCode());

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
                    return dealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, null);
                }

                return dealCodeResponder.dealWrong(id, err, comCode, comRes, comErr);

            }

            //other languages run success
            if (!Lang.equals("cpp") && !Lang.equals("go")){
                return dealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, null);
            }
            //c or go compile success start to run
            if (Lang.equals("cpp")) command = "runCpp.sh";
            if (Lang.equals("go")) command = "runGo.sh";
            cmdarray = new String[]{"./" + command, runArgs, id};
            Process processSub = Runtime.getRuntime().exec(cmdarray, null, comPath);
            processSub.waitFor();

            //kill the pid, avoid something bad happen
            if (!processSub.isAlive()) processSub.destroy();

            return dealCodeResponder.dealRight(id, comCode, comRes, comInfo, comErr, comExe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> saveCode(Code code, String email){
        Map<String, Object> resulteMap = new HashMap<>();

        List<Code> codes = codeMapper.SelectUniqueCode(code, email);
        if (codes != null && codes.size() > 0){
            resulteMap.put("code", 300); resulteMap.put("msg", "文件已经存在了，换个名字吧 (´･д･｀)");
            return resulteMap;
        }

        LocalDateTime ldt = LocalDateTime.now();
        int judge = codeMapper.InsertCode(code, email, ldt);

        if (judge == 1){
            resulteMap.put("code", 200);
            resulteMap.put("msg", "保存成功 (・∀・)");

            Thread thread = new Thread(new SaveCodeFileThread(code, email, codeSavePath));
            thread.start();
        } else {
            resulteMap.put("code", 400);
            resulteMap.put("msg", "保存失败,请联系管理员 (´･д･｀)");
        }

        return resulteMap;
    }

    public Map<String, Object> deleteCode(Code code, String email){
        int judge = codeMapper.DeleteCode(code, email);

        Map<String, Object> resultMap = new HashMap<>();
        if (judge == 1) {
            resultMap.put("code", 200);
            resultMap.put("msg", "删除成功! (・∀・)");

            Thread thread = new Thread(new DeleteCodeFileThread(code, email, codeSavePath));
            thread.start();
        } else {
            resultMap.put("code", 400);
            resultMap.put("msg", "删除失败,请联系管理员 （´(ｪ)｀）");
        }

        return resultMap;
    }

    public Page<Code> getCodes(String email){
        return codeMapper.SelectCodesPageByEmail(email);
    }
}

