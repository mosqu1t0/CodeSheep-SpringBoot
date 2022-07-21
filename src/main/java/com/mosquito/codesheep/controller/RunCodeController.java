package com.mosquito.codesheep.controller;


import com.mosquito.codesheep.pojo.Code;
import com.mosquito.codesheep.pojo.CodeResponder;
import com.mosquito.codesheep.service.RunCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
public class RunCodeController {

    @Autowired
    RunCodeService runCode;

    @PostMapping("/code")
    CodeResponder handleRunCodePost(@RequestBody Code code, HttpSession session){
        CodeResponder codeResponder = runCode.compileCode(code, session.getId());
        return codeResponder;
    }
}
