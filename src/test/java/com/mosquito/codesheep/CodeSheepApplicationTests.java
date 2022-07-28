package com.mosquito.codesheep;

import cn.hutool.jwt.JWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeSheepApplicationTests {

    @Test
    void contextLoads() {
        byte[] key = "0000000000".getBytes();
        String token = JWT.create()
                .setPayload("name","admin")
                .setPayload("what", "cool")
                .setKey(key)
                .sign();
        System.out.println(token);

        JWT jwt = JWT.of(token);
        String what = (String) jwt.getPayload("what");
        String name = (String) jwt.getPayload("name");
        System.out.println(what + ' ' + name);

    }

}
