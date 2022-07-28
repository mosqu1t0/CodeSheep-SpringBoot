package com.mosquito.codesheep.thread;

import com.mosquito.codesheep.service.EmailService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
@Slf4j
public class DeleteCodeThread implements Runnable{

    private final File comCode;
    private final File comRes;
    private final File comInfo;
    private final File comErr;

    private final File comExe;

    private final String id;
    private final Set<Object> wrongSet = new HashSet<>();

    public DeleteCodeThread(File comCode, File comRes, File comInfo, File comErr, File comExe, String id) {
        this.comCode = comCode;
        this.comRes = comRes;
        this.comInfo = comInfo;
        this.comErr = comErr;
        this.comExe = comExe;
        this.id = id;
    }

    @Override
    public void run() {
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
