package com.mosquito.codesheep.service;


import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mosquito.codesheep.mapper.CodeMapper;
import com.mosquito.codesheep.mapper.UserMapper;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.thread.DeleteCodeFileThread;
import com.mosquito.codesheep.thread.SaveCodeFileThread;
import com.mosquito.codesheep.utils.DealCodeResponderUtil;
import com.mosquito.codesheep.utils.languageMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    DealCodeResponderUtil dealCodeResponderUtil;
    @Resource
    CodeMapper codeMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;
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
        String codePath = languageMapUtil.getPath(Lang);
        String suffix = languageMapUtil.getSuffix(Lang);
        String command = languageMapUtil.getCommand(Lang);

        File comPath = new File(codeWorkPath);
        File comCode = new File(codeWorkPath + codePath + id + suffix);
        File comInfo = new File(codeWorkPath + codePath + id + ".info");
        File comOut = new File(codeWorkPath + codePath + id + ".out");
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
            if (!languageMapUtil.secondCommand.containsKey(Lang)){
                // compile with run
                cmdarray = new String[]{"./" + command, runArgs, id};
            } else {
                // compile c or cpp
                cmdarray = new String[]{"./" + command, id};
            }

            Process process = Runtime.getRuntime().exec(cmdarray , null, comPath);
            process.waitFor(); // wait for the pid is ending, and the exitvalue will be 1
            //kill the pid, avoid something bad happen
            if (!process.isAlive()) process.destroy();

            //compile error
            if (comErr.exists() && comErr.length() > 0) {
                String err = new String(Files.readAllBytes(Paths.get(comErr.getPath())));

                //dangerous wrong only script language could happen
                if (err.equals("killWrong\n")){
                    return dealCodeResponderUtil.dealRight(id, comCode, comOut, comInfo, comErr, null);
                }

                return dealCodeResponderUtil.dealWrong(id, err, comCode, comOut, comErr);

            }

            //script languages run success
            if (!languageMapUtil.secondCommand.containsKey(Lang)){
                return dealCodeResponderUtil.dealRight(id, comCode, comOut, comInfo, comErr, null);
            }
            //c or cpp compile success start to run
            command = languageMapUtil.getSecondCommand(Lang);
            cmdarray = new String[]{"./" + command, runArgs, id};
            Process processSub = Runtime.getRuntime().exec(cmdarray, null, comPath);
            processSub.waitFor();

            //kill the pid, avoid something bad happen
            if (!processSub.isAlive()) processSub.destroy();

            return dealCodeResponderUtil.dealRight(id, comCode, comOut, comInfo, comErr, comExe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> saveCode(Code code, String email){
        Map<String, Object> resultMap = new HashMap<>();

        // get user id
        Integer uid = userMapper.SelectUidByEmail(email);
        // check if user have save the same name
        List<Code> codes = codeMapper.SelectUniqueCode(code, uid);
        if (codes != null && codes.size() > 0){
            resultMap.put("code", 300);
            resultMap.put("msg", "文件已经存在了，换个名字吧 (´･д･｀)");
            return resultMap;
        }

        LocalDateTime ldt = LocalDateTime.now();
        // insert a code field
        int judge = codeMapper.InsertCode(code, uid, ldt);

        if (judge == 1){
            resultMap.put("code", 200);
            resultMap.put("msg", "保存成功 (・∀・)");

            // using thread to deal with the unnecessary io waiting time
            threadPoolTaskExecutor.execute(new SaveCodeFileThread(code, email, codeSavePath));
        } else {
            resultMap.put("code", 400);
            resultMap.put("msg", "保存失败,请联系管理员 (´･д･｀)");
        }

        return resultMap;
    }

    public Map<String, Object> deleteCode(Code code, String email){
        Integer uid = userMapper.SelectUidByEmail(email);
        int judge = codeMapper.DeleteCode(code, uid);

        Map<String, Object> resultMap = new HashMap<>();
        if (judge == 1) {
            resultMap.put("code", 200);
            resultMap.put("msg", "删除成功! (・∀・)");

            // using thread to deal with the unnecessary io waiting time
            threadPoolTaskExecutor.execute(new DeleteCodeFileThread(code, email, codeSavePath));
        } else {
            resultMap.put("code", 400);
            resultMap.put("msg", "删除失败,请联系管理员 （´(ｪ)｀）");
        }

        return resultMap;
    }

    public Page<Code> getCodes(Integer pageSize, Integer pageNum, String language, String fileName, String email){
        // same, get user id by email
        Integer uid = userMapper.SelectUidByEmail(email);
        // get pages div
        PageHelper.startPage(pageNum, pageSize);
        return codeMapper.SelectCodesPageByCondition(uid, language, fileName);
    }

    public Map<String, Object> getCode(Code code, String email){
        // user id
        Integer uid = userMapper.SelectUidByEmail(email);
        List<Code> codes = codeMapper.SelectUniqueCode(code, uid);

        Map<String, Object> resultMap = new HashMap<>();
        if (codes == null || codes.size() != 1){
            resultMap.put("code", "400");
            resultMap.put("msg", "没有找到代码 (´･д･｀)");
        } else {
            // necessary sync io read code file
            String suffix = languageMapUtil.getSuffix(code.getLanguage());
            FileReader fileReader = new FileReader(codeSavePath + email + '/' + code.getFileName() + suffix);
            String codeContent = fileReader.readString();

            resultMap.put("code", 200);
            resultMap.put("msg", "打开成功 (・∀・)");
            resultMap.put("content", codeContent);
        }
        return resultMap;
    }
    public Map<String, Object> UpdateCode(Code code, String email){
        // once updating a code,update the time firstly
        code.setTime(LocalDateTime.now());
        Integer uid = userMapper.SelectUidByEmail(email);
        int judge = codeMapper.UpdateCodeTime(code, uid);

        Map<String, Object> resultMap = new HashMap<>();
        if (judge == 1){
            resultMap.put("code", 200);
            resultMap.put("msg", "保存成功 (・∀・)");

            // use thread to update code file
            threadPoolTaskExecutor.execute(new SaveCodeFileThread(code, email, codeSavePath));
        } else {
            resultMap.put("code", 400);
            resultMap.put("msg", "更新失败,请联系管理员 （´(ｪ)｀）");
        }

        return resultMap;
    }
}

