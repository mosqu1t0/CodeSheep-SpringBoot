package com.mosquito.codesheep.controller;


import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.service.CodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
}
