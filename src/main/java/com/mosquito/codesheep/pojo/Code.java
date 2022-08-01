package com.mosquito.codesheep.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    private String language;
    private String code;
    private String input;
    private String fileName;
    private LocalDateTime time;
}
