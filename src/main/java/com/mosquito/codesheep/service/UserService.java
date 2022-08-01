package com.mosquito.codesheep.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import com.mosquito.codesheep.mapper.UserMapper;
import com.mosquito.codesheep.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {
    @Resource
    UserMapper userMapper;

    @Resource
    EmailService emailService;

    @Value("${jwt.key}")
    String jwtKey;
    public Map<String, Object> creatAccount(User user){
        Map<String, Object> resultMap = new HashMap<>();
        List<User> queryUsers = userMapper.SelectUserByEmail(user.getEmail());

        if (queryUsers != null && !queryUsers.isEmpty()){
            resultMap.put("code", 244);
            resultMap.put("msg", "该账户已经注册过了喔，如果未激活请到邮箱激活 U￣ｰ￣U");
            return resultMap;
        }

        //snowflake
        String confirmCode = IdUtil.getSnowflake(1, 1).nextIdStr();
        String salt = RandomUtil.randomNumbers(6);
        //savePasswd = oldpasswd + salt --> md5
        String md5Passwd = SecureUtil.md5(user.getPassword() + salt);
        LocalDateTime ldt = LocalDateTime.now().plusDays(1);

        user.setConfirmCode(confirmCode);
        user.setSalt(salt);
        user.setIsVaild((byte) 0);
        user.setPassword(md5Passwd);
        user.setActivationTime(ldt);

        //新增帐号
        int result = userMapper.InsertUser(user);

        if (result > 0){
            resultMap.put("code", "200");
            resultMap.put("msg", "注册成功啦，快去邮箱激活帐号吧! Y(･∀･)Y");

            emailService.sendMailForActivationAccount(confirmCode, user.getEmail());
        } else {
            resultMap.put("code", "400");
            resultMap.put("msg", "由于很神奇的原因，注册失败了 ∑(￣□￣)");
        }

        return resultMap;
    }

    public Map<String, Object> loginAccount(User user, boolean remember, HttpServletResponse response){
        Map<String, Object> resultMap = new HashMap<>();

        List<User> queryUsers = userMapper.SelectUserByEmailAndVaild(user.getEmail());

        if (queryUsers == null || queryUsers.isEmpty()) {
            resultMap.put("code", 404);
            resultMap.put("msg", "账户不存在或者未激活 （´(ｪ)｀）");
            return resultMap;
        }

        if (queryUsers.size() > 1) {
            resultMap.put("code", 405);
            resultMap.put("msg", "账户异常，请停止登录，联系管理员处理 ∑(￣□￣)");

            log.error("账号异常: {}", user.getEmail());
            String bugInfo = "这个账号出现异常: " + user.getEmail();
            emailService.sendMailForBugs(bugInfo);

            return resultMap;
        }
        User queryUser = queryUsers.get(0);
        String md5Passwd = SecureUtil.md5(user.getPassword() + queryUser.getSalt());

        if (queryUser.getPassword().equals(md5Passwd)){
            resultMap.put("code", 200);
            resultMap.put("msg", "登录成功! Y(･∀･)Y");

            String token = JWT.create()
                    .setPayload("email", queryUser.getEmail())
                    .setKey(jwtKey.getBytes())
                    .sign();

            Cookie cookie = new Cookie("token", token);
            if (remember) cookie.setMaxAge(604800);
            else cookie.setMaxAge(86400);
            cookie.setPath("/");

            response.addCookie(cookie);
        } else {
            resultMap.put("code", 244);
            resultMap.put("msg", "密码错误... （´(ｪ)｀）");
        }

        return resultMap;
    }

    public int activateAccount(String confirmCode){
        User queryUser = userMapper.SelectUserByConfirmCode(confirmCode);

        if (queryUser == null) return 0;

        if (LocalDateTime.now().isBefore(queryUser.getActivationTime())){
            return userMapper.UpdataUserByConfirmCode(confirmCode);
        }

        return 3;
    }

}
