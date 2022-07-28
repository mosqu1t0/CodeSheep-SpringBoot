package com.mosquito.codesheep.controller;

import com.mosquito.codesheep.pojo.User;
import com.mosquito.codesheep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/user")
    @ResponseBody
    Map<String, Object> handleRegister(@RequestBody User user){
        return userService.creatAccount(user);
    }

    @PostMapping("/user-login")
    @ResponseBody
    Map<String, Object> handleLogin(@RequestBody User user, HttpServletResponse response) {
        System.out.println(user.getRemember());
        return userService.loginAccount(user, user.getRemember(), response);
    }

    @GetMapping("/user")
    String handleActivation(@RequestParam String confirmCode){
        int judge = userService.activateAccount(confirmCode);
        if (judge == 1) return "success";
        if (judge == 3) return "overtime";
        return "failure";
    }
}
