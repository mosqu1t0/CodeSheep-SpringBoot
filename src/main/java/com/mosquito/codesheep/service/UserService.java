package com.mosquito.codesheep.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.mosquito.codesheep.mapper.UserMapper;
import com.mosquito.codesheep.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    EmailService emailService;
    public Map<String, Object> creatAccount(User user){
        Map<String, Object> resMap = new HashMap<>();
        List<User> queryUsers = userMapper.SelectUserByEmail(user.getEmail());

        if (queryUsers != null && !queryUsers.isEmpty()){
            resMap.put("code", 244);
            resMap.put("msg", "该账户已经注册过了喔，如果未激活请到邮箱激活 U￣ｰ￣U");
            return resMap;
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
            resMap.put("code", "200");
            resMap.put("msg", "注册成功啦，快去邮箱激活帐号吧! Y(･∀･)Y");
            emailService.sendMailForActivationAccount(confirmCode, user.getEmail());
        } else {
            resMap.put("code", "400");
            resMap.put("msg", "由于很神奇的原因，注册失败了 ∑(￣□￣)");
        }

        return resMap;
    }

    public Map<String, Object> loginAccount(User user){
        Map<String, Object> resMap = new HashMap<>();

        List<User> queryUsers = userMapper.SelectUserByEmailAndVaild(user.getEmail());

        if (queryUsers == null || queryUsers.isEmpty()) {
            resMap.put("code", 404);
            resMap.put("msg", "账户不存在或者未激活 （´(ｪ)｀）");
            return resMap;
        }

        if (queryUsers.size() > 1) {
            resMap.put("code", 405);
            resMap.put("msg", "账户异常，请联系管理员处理 ∑(￣□￣)");
            //todo account error email
            return resMap;
        }
        User queryUser = queryUsers.get(0);
        String md5Passwd = SecureUtil.md5(user.getPassword() + queryUser.getSalt());

        if (queryUser.getPassword().equals(md5Passwd)){
            resMap.put("code", 200);
            resMap.put("msg", "登录成功! Y(･∀･)Y");
        } else {
            resMap.put("code", 244);
            resMap.put("msg", "密码错误... （´(ｪ)｀）");
        }

        return resMap;
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
