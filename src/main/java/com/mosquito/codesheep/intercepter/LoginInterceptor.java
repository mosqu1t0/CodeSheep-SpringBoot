package com.mosquito.codesheep.intercepter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${jwt.key}")
    String jwtKey;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Cookie[] cookies = request.getCookies();
        String token = "";
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    token = cookie.getValue();
                    break;
                }
            }

            if (token.equals("")) return false;

            if (JWTUtil.verify(token, jwtKey.getBytes())){
                JWT jwt = JWT.of(token);
                String email = (String) jwt.getPayload("email");
                request.setAttribute("email", email);
                return true;
            }
        }
        return false;
    }
}
