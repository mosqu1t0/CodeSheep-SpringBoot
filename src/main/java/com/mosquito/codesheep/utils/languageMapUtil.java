package com.mosquito.codesheep.utils;


import java.util.HashMap;
import java.util.Map;

public class languageMapUtil {

    public static Map<String, String> suffix = new HashMap<>();
    public static Map<String, String> command = new HashMap<>();
    public static Map<String, String> secondCommand = new HashMap<>();
    public static Map<String, String> path = new HashMap<>();

    static {
        suffix.put("cpp", ".cpp");
        suffix.put("go", ".go");
        suffix.put("python", ".py");
        suffix.put("javascript", ".js");

        command.put("cpp", "compileCpp.sh");
        command.put("python", "runPy.sh");
        command.put("javascript", "runJs.sh");

        secondCommand.put("cpp", "runCpp.sh");
        secondCommand.put("go", "runGo.sh");

        path.put("cpp", "Cpp/Cpp");
        path.put("go", "Go/Go");
        path.put("python", "Py/Py");
        path.put("javascript", "Js/Js");
    }
    public static String getSuffix(String language){
        return suffix.get(language);
    }
    public static String getCommand(String language){
        return command.get(language);
    }
    public static String getSecondCommand(String language){
        return secondCommand.get(language);
    }
    public static String getPath(String language){
        return path.get(language);
    }
}
