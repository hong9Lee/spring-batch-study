package com.pjt.springbatchstudy.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private final JobOperator jobOperator;
    private final Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user1")
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        jobOperator.start(job, jobParameters);
    }
}



// 아래는 SpringBatch4 버전
//public class JobRunner implements ApplicationRunner {
//
//    private final JobLauncher jobLauncher;
//    private final Job job;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("name", "user1")
//                .addLong("run.id", System.currentTimeMillis())
//                .toJobParameters();
//
//        jobLauncher.run(job, jobParameters);
//    }
//}
