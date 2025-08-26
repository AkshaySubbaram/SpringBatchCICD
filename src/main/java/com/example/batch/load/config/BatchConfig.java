package com.example.batch.load.config;

import com.example.batch.load.entity.MsJobs;
import com.example.batch.load.entity.MsTasks;
import com.example.batch.load.entity.TnSkillUploadDetails;
import com.example.batch.load.entity.TnUpload;
import com.example.batch.load.listener.JobCompletionNotificationListener;
import com.example.batch.load.processor.TaskUploadProcessor;
import com.example.batch.load.reader.ExcelItemReader;
import com.example.batch.load.reader.MsTaskReader;
import com.example.batch.load.row.mapper.SkillStepListener;
import com.example.batch.load.service.JobTaskTnUpload;
import com.example.batch.load.writer.JobWriter;
import com.example.batch.load.writer.SkillItemWriter;
import com.example.batch.load.writer.TaskWriter;
import com.example.batch.load.writer.TnUploadWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobTaskTnUpload jobTaskTnUpload;

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       JobTaskTnUpload jobTaskTnUpload) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobTaskTnUpload = jobTaskTnUpload;
    }

    @Bean
    public Job updateTnUploadJob() {
        logger.info("Registering updateTnUploadJob bean");
        return new JobBuilder("updateTnUploadJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateJobsStep())
                .next(updateTasksStep())
                .build();
    }

    @Bean
    public Step updateJobsStep() {
        return new StepBuilder("updateJobsStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    logger.info("Executing job IDs update...");
                    jobTaskTnUpload.updateTnUploadWithJobIds();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step updateTasksStep() {
        logger.info("Creating updateTasksStep");
        return new StepBuilder("updateTasksStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    logger.info("Executing task IDs update...");
                    jobTaskTnUpload.updateTnUploadWithTaskIds();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    //  1. Skill Job
    @Bean
    public Job importSkillsJob(JobRepository jobRepository,
                               Step skillLoadStep,
                               JobCompletionNotificationListener listener) {
        logger.info("importSkillsJob----------------");
        return new JobBuilder("importSkillsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(skillLoadStep)
                .build();
    }

    //  2. MsJob data
    @Bean
    public Job importMsJobJob(JobRepository jobRepository,
                              Step msJobLoadStep,
                              JobCompletionNotificationListener listener) {
        logger.info("inside importMsJobJob");
        return new JobBuilder("importMsJobJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(msJobLoadStep)
                .build();
    }

    //  3. MsTask data
    @Bean
    public Job importMsTaskJob(JobRepository jobRepository,
                               Step msTaskLoadStep,
                               JobExecutionListener listener) {
        return new JobBuilder("importMsTaskJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(msTaskLoadStep)
                .build();
    }

    @Bean
    public Step skillLoadStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemReader<TnSkillUploadDetails> reader,
                              SkillItemWriter writer,
                              SkillStepListener listener) {
        logger.info("skillLoadStep::::::::::::");
        return new StepBuilder("skillLoadStep", jobRepository)
                .<TnSkillUploadDetails, TnSkillUploadDetails>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .listener(listener)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    //  MS Job data
    @Bean
    public Step msJobLoadStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ExcelItemReader<MsJobs> msJobReader,
                              JobWriter writer) {
        logger.info("inside msJobLoadStep");
        return new StepBuilder("msJobLoadStep", jobRepository)
                .<MsJobs, MsJobs>chunk(10, transactionManager)
                .reader(msJobReader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Step msTaskLoadStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               MsTaskReader msTaskReader,
                               TaskWriter writer) {
        return new StepBuilder("msTaskLoadStep", jobRepository)
                .<MsTasks, MsTasks>chunk(10, transactionManager)
                .reader(msTaskReader)
                .writer(writer)
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Job importJob(JobRepository jobRepository,
                         @Qualifier("importJobStep") Step importJobStep,
                         JobExecutionListener listener) {
        return new JobBuilder("importJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(importJobStep)
                .build();
    }

    @Bean("importJobStep")
    public Step importJobStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemReader<MsJobs> importJobReader,
                              JobWriter importJobWriter) {
        return new StepBuilder("importJobStep", jobRepository)
                .<MsJobs, MsJobs>chunk(10, transactionManager)
                .reader(importJobReader)
                .writer(importJobWriter)
                .build();
    }

    @Bean
    public Step tnUploadStep(JobRepository jobRepository,
                             PlatformTransactionManager txManager,
                             ItemReader<MsTasks> taskReader,
                             TaskUploadProcessor processor,
                             TnUploadWriter writer) {
        logger.info("inside TnUpload batch");
        return new StepBuilder("tnUploadStep", jobRepository)
                .<MsTasks, TnUpload>chunk(10, txManager)
                .reader(taskReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}

