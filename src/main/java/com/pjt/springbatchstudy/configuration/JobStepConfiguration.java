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
public class JobStepConfiguration {

    private final JobRepository jobRepository;

    @Bean
    public Job jobStepJob() {
        return new JobBuilder("jobStepJob", jobRepository)
                .start(jobStep1())
                .next(jobStep2())
                .build();
    }

    @Bean
    public Step jobStep1() {
        return new StepBuilder("jobStep1", jobRepository)
                .tasklet(new CustomTasklet())
                .build();
    }

    @Bean
    public Step jobStep2() {
        return new StepBuilder("jobStep2", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("jobStep2 was executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
