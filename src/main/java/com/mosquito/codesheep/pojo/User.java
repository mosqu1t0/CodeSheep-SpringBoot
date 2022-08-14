package com.mosquito.codesheep.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String email;
    private String password;
    private String salt;
    private String confirmCode;
    private LocalDateTime activationTime; //失效时间
    private Byte isValid;
    private String config;
}
