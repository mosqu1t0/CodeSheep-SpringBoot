package com.mosquito.codesheep.service;


import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.pojo.CodeResponder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;


@Service
public class RunCodeService {
    @Value("${codeWork.path}")
    String codeWorkPath;

    public CodeResponder compileCode(Code code, String id){
        CodeResponder CodeRes = new CodeResponder();
        File comPath = new File(codeWorkPath);
        if (!comPath.exists()) comPath.mkdir();

        String Lang = code.getLang();
        String runArgs = code.getInput();
        String codeName = "";
        String suffixName = "";
        String command = "";

        switch (Lang) {
            case "cpp":
                codeName += "Cpp" + id;
                suffixName = ".cpp";
                command = "compileCpp.sh";
                break;
            case "java":
                codeName += "Java" + id;
                suffixName = ".java";
                command = "runJava.sh";
                break;
            case "python":
                codeName += "Py" + id;
                suffixName = ".py";
                command = "runPy.sh";
                break;
            case "javascript":
                codeName += "Js" + id;
                suffixName = ".js";
                command = "runJs.sh";
                break;
            default:
                break;
        }

        File comCode = new File(codeWorkPath + codeName + suffixName);
        File comInfo = new File(codeWorkPath + codeName + ".info");
        try {
            FileWriter writer = new FileWriter(comCode.getPath());
            writer.write(code.getCode());
            writer.flush();
            writer.close();

            // execute .sh
            String[] cmdarray = {"./"+ command, id};
            Process process = Runtime.getRuntime().exec(cmdarray , null, comPath);
            process.waitFor(); // wait for the pid is ending, and the exitvalue will be 1

            //kill the pid, avoid something bad happen
            if (!process.isAlive()) process.destroy();

            // others
            if (Lang != "cpp" && Lang != "go"){
                //todo other languages support
            }

            //编译失败
            if (comInfo.exists() && comInfo.length() > 0) {
                String content = new String(Files.readAllBytes(Paths.get(comInfo.getPath())));
                CodeRes.setCode(244);
                CodeRes.setRes(content);
                CodeRes.setMsg("Nop");
                if (!comInfo.delete() || !comCode.delete()) CodeRes.setMsg("出错啦，快联系管理员");
                return CodeRes;
            }

            //编译成功，开始执行
            if (Lang.equals("cpp")) command = "runCpp.sh";
            if (Lang.equals("go")) command = "runGo.sh";
            String[] Args = {"./" + command, runArgs, id};
            Process processSub = Runtime.getRuntime().exec(Args, null, comPath);
            processSub.waitFor();

            //kill the pid, avoid something bad happen
            if (!processSub.isAlive()) processSub.destroy();

            File comExe = new File(codeWorkPath + codeName);
            File comRes = new File(codeWorkPath + codeName + ".out");
            String content = null;
            if (comRes.exists() && comRes.length() > 0) {
                content = new String(Files.readAllBytes(Paths.get(comRes.getPath())));
            }
            if (comInfo.exists() && comInfo.length() > 0) {
                String info = new String(Files.readAllBytes(Paths.get(comInfo.getPath())));
                content += info;
                CodeRes.setCode(233);
                CodeRes.setRes(content);
                CodeRes.setMsg("Long time error...");
            } else {
                CodeRes.setCode(200);
                CodeRes.setRes(content);
                CodeRes.setMsg("Good!");
            }

            if (!comCode.delete() || !comInfo.delete() || !comRes.delete() || !comExe.delete()) CodeRes.setMsg("出错啦，快管理员");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return CodeRes;
    }
}
