package com.mosquito.codesheep.utils;

import com.mosquito.codesheep.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ScheduleUtil {
    @Resource
    UserMapper userMapper;

    @Scheduled(cron = "0 55 23 * * ?")
    public void removeInActivatedAccounts() {
        Integer judge = userMapper.DeleteInactivatedAccounts();
        log.info("There are {} inactivated accounts being deleted.", judge);
    }
}
