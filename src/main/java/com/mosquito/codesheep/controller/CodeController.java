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

    @DeleteMapping("/code/{language}/{fileName}")
    Map<String, Object> handleDeleteCode(
            @PathVariable String fileName,
            @PathVariable String language ,
            HttpServletRequest request
    ){
        return codeService.deleteCode(new Code(language, null, null, fileName, null),
                (String) request.getAttribute("email"));
    }

    @PutMapping("/code")
    Map<String, Object> handleUpdateCode(@RequestBody Code code, HttpServletRequest request){
        return codeService.UpdateCode(code, (String) request.getAttribute("email"));
    }

    @GetMapping("/code/{language}/{fileName}")
    Map<String, Object> handleOpenCode(
            @PathVariable String language,
            @PathVariable String fileName,
            HttpServletRequest request
    ){
        return codeService.getCode(new Code(language, null, null, fileName, null),
                (String) request.getAttribute("email"));
    }

    @GetMapping("/codes/{pageSize}/{pageNum}")
    PageInfo<Code> handleGetCodes(
            @PathVariable Integer pageSize,
            @PathVariable Integer pageNum,
            HttpServletRequest request
    ){
        PageHelper.startPage(pageNum, pageSize);
        return codeService.getCodes((String) request.getAttribute("email")).toPageInfo();
    }

}
