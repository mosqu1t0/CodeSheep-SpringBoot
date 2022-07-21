package com.mosquito.codesheep.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponder {
    private Integer code;
    private String msg;
    private String res;
}
