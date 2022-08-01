package com.mosquito.codesheep.thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
@AllArgsConstructor
@Slf4j
public class CleanCodeThread implements Runnable{

    private final File comCode;
    private final File comRes;
    private final File comInfo;
    private final File comErr;

    private final File comExe;

    private final String id;

    @Override
    public void run() {
        Set<Object> wrongSet = new HashSet<>();

        if (comCode != null && comCode.exists() && !comCode.delete()) wrongSet.add(comCode);
        if (comRes != null && comRes.exists() && !comRes.delete()) wrongSet.add(comRes);
        if (comInfo != null && comInfo.exists() && !comInfo.delete()) wrongSet.add(comInfo);
        if (comErr != null && comErr.exists() && !comErr.delete()) wrongSet.add(comErr);
        if (comExe != null && comExe.exists() && !comExe.delete()) wrongSet.add(comExe);

        if (!wrongSet.isEmpty()) {
            log.error("Can't delete these: {} from {}", wrongSet, id);
        }
    }
}
