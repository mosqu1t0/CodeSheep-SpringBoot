package com.mosquito.codesheep.controller;

import com.mosquito.codesheep.pojo.User;
import com.mosquito.codesheep.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/user")
    Map<String, Object> handleRegister(@RequestBody User user){
        return userService.creatAccount(user);
    }

    @PostMapping("/user-login")
    Map<String, Object> handleLogin(@RequestBody User user) {
        System.out.println(user);
        return userService.loginAccount(user);
    }

    @GetMapping("/user")
    String handleActivation(@RequestParam String confirmCode){
        int judge = userService.activateAccount(confirmCode);
        if (judge == 1) return "success";
        if (judge == 3) return "failure";
        return "error";
    }
}
