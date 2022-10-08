package com.mosquito.codesheep.utils;


import java.util.HashMap;
import java.util.Map;

public class languageMapUtil {

    public static Map<String, String> suffix = new HashMap<>();
    public static Map<String, String> command = new HashMap<>();
    public static Map<String, String> secondCommand = new HashMap<>();
    public static Map<String, String> path = new HashMap<>();

    static {
        suffix.put("c", ".c");
        suffix.put("cpp", ".cpp");
        suffix.put("rust", ".rs");
        suffix.put("golang", ".go");
        suffix.put("python", ".py");
        suffix.put("javascript", ".js");

        command.put("c", "compileC.sh");
        command.put("cpp", "compileCpp.sh");
        command.put("rust", "compileRust.sh");
        command.put("golang", "compileGo.sh");
        command.put("python", "runPy.sh");
        command.put("javascript", "runJs.sh");

        secondCommand.put("c", "runC.sh");
        secondCommand.put("cpp", "runCpp.sh");
        secondCommand.put("rust", "runRust.sh");
        secondCommand.put("golang", "runGo.sh");

        path.put("c", "C/C");
        path.put("cpp", "Cpp/Cpp");
        path.put("rust", "Rust/Rust");
        path.put("golang", "Go/Go");
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
