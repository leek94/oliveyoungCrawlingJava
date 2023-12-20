package com.example.cosmeticCrawlingJava.batchConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class BatchScheduler {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Batch batch;

    //크론식 초 , 분 , 시 , 일 , 월 , 요일  * * * * * *
        @Scheduled(cron = "* 54 15 * * * ") // 매일 오후 12시20분, 오후 6시20분 실행
//    @Scheduled(cron = "0 */3 * * * *") // 3분 마다
    public void runJob() {

        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);

        try {
            jobLauncher.run(batch.TaskletJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException | JobRestartException e) {
            log.error(e.getMessage());
        }

    }
}
