package com.mosquito.codesheep.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.service.CodeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
public class CodeController {

    @Resource
    CodeService codeService;

    @PostMapping("/code-run")
    Map<String, Object> handleRunCode(@RequestBody Code code, HttpSession session){
        return codeService.runCode(code, session.getId());
    }

    @PostMapping("/code")
    Map<String, Object> handleSaveCode(@RequestBody Code code, HttpServletRequest request){
        String email = (String) request.getAttribute("email");
        return codeService.saveCode(code, email);
    }

    @DeleteMapping("/code")
    Map<String, Object> handleDeleteCode(
            @RequestParam String fileName,
            @RequestParam String language ,
            HttpServletRequest request
    ){
        return codeService.deleteCode(new Code(language, null, null, fileName, null),
                (String) request.getAttribute("email"));
    }

    @GetMapping("/code")
    PageInfo<Code> handleGetCode(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            HttpServletRequest request
    ){
        PageHelper.startPage(pageNum, pageSize);
        return codeService.getCodes((String) request.getAttribute("email")).toPageInfo();
    }

}
