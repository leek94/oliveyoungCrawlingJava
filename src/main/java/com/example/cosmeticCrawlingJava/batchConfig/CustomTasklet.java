package com.example.cosmeticCrawlingJava.batchConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;

@Slf4j
public class CustomTasklet implements Tasklet, StepExecutionListener {

    public int a = 0;

    @Override
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("======================= Crawling 시작 ======================= : " + LocalDateTime.now());
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("======================= Crawling 완료 =======================");

        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.info("배치중");

        log.info(String.valueOf(a++));

        return RepeatStatus.FINISHED;
    }
}
