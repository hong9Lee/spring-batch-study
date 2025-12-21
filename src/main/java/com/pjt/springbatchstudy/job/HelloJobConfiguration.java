package com.pjt.springbatchstudy.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job helloJob() {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep())
                .build();
    }

    @Bean
    public Step helloStep() {
        return new StepBuilder("helloStep", jobRepository)
                .tasklet((contribution, cunkContext) -> {
                    System.out.println("Hello Spring Batch");
                    return RepeatStatus.FINISHED;
                }).build();
    }


}
