package com.example.cosmeticCrawlingJava.batchConfig;

import com.example.cosmeticCrawlingJava.service.Crawling;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomTasklet implements Tasklet, StepExecutionListener {

    private final Crawling crawling;

    @Override
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("======================= Crawling 시작 =======================");
    }

    @Override
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("======================= Crawling 완료 =======================");

        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        crawling.startCrawling();

        return RepeatStatus.FINISHED;
    }
}
