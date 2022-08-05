package com.mosquito.codesheep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mosquito.codesheep.pojo.User;
import com.mosquito.codesheep.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    Map<String, Object> handleLogin(@RequestBody Map<String, Object> map, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.convertValue(map.get("user"), User.class);
        boolean remember = (boolean) map.get("remember");

        return userService.loginAccount(user, remember, response);
    }

    @PutMapping("/user/config")
    @ResponseBody
    Map<String, Object> handleUploadConfigs(@RequestBody String config, HttpServletRequest request){
        return userService.uploadConfig(config, (String) request.getAttribute("email"));
    }

    @GetMapping("/user/{confirmCode}")
    String handleActivation(@PathVariable String confirmCode, HttpServletRequest request){
        userService.activateAccount(confirmCode, request);
        return "active-info";
    }
}
