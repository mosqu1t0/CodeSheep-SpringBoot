package com.mosquito.codesheep.tools;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;


@Component
public class PreDone {
    static String CodeWorkPath;

    @Value("${codeWork.path}")
    public void setCodeWorkPath(String codeWorkPath) {
        CodeWorkPath = codeWorkPath;
    }

    public static void addShToWorkPath(){
        File path = null;
        try {
            path = ResourceUtils.getFile("classpath:sh");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        File[] tempList = path.listFiles();

        for (int i = 0 ; i < tempList.length ; i ++){
            if (tempList[i].isFile()) {
                String fileName = tempList[i].getName();
                try {
                    File sh = new File(CodeWorkPath + fileName);
                    System.out.println(CodeWorkPath);
                    FileInputStream fileInputStream = new FileInputStream(tempList[i]);
                    FileOutputStream fileOutputStream = new FileOutputStream(sh);
                    byte[] bytes = new byte[(int) tempList[i].length()];
                    fileInputStream.read(bytes);
                    fileOutputStream.write(bytes);
                    sh.setExecutable(true);
                    fileOutputStream.close();
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
