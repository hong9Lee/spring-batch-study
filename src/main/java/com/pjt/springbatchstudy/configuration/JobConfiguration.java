package com.pjt.springbatchstudy.configuration;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobRepository jobRepository;

//    @Bean
    public Job basicJob() {
        return new JobBuilder("basicJob", jobRepository)
                .start(basicStep1())
                .next(basicStep2())
                .build();
    }

//    @Bean
    public Step basicStep1() {
        return new StepBuilder("basicStep1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("basicStep1 was executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

//    @Bean
    public Step basicStep2() {
        return new StepBuilder("basicStep2", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("basicStep2 was executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
