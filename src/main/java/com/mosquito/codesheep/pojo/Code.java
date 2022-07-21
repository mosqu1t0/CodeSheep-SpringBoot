package com.mosquito.codesheep.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    private String lang;
    private String code;
    private String input;

}
