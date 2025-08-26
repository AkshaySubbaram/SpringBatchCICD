package com.example.batch.load.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

@Component
public class BatchJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobScheduler.class);

    private final JobLauncher jobLauncher;

    private final Job importSkillsJob;

    private final JobExplorer jobExplorer;

    private final Timer jobTimer;

    private final Counter jobRunCounter;

    private final Timer jobExecutionTimer;

    @Autowired
    public BatchJobScheduler(JobLauncher jobLauncher,
                             Job importSkillsJob,
                             JobExplorer jobExplorer,
                             MeterRegistry meterRegistry) {
        this.jobLauncher = jobLauncher;
        this.importSkillsJob = importSkillsJob;
        this.jobExplorer = jobExplorer;
        this.jobTimer = meterRegistry.timer("batch.job.execution.time", "job", "importSkillsJob");

        // Custom Prometheus metrics
        this.jobRunCounter = meterRegistry.counter("batch.jobs.total");
        this.jobExecutionTimer = meterRegistry.timer("batch.jobs.execution.time");
    }


//    @Scheduled(cron = "0 0 2 * * *")    // Runs at 2 AM every day
    @Scheduled(cron = "0 0 */6 * * *") // 6hrs
//    @Scheduled(cron = "0 */1 * * * *")
    public void runBatchJob() {
        logger.info("Scheduled Job Triggered...");

        jobTimer.record(() -> {
            try {
                JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                        .addLong("startTime", System.currentTimeMillis())
                        .toJobParameters();

                JobExecution execution = jobLauncher.run(importSkillsJob, jobParameters);
                logger.info("Scheduled Job Status: {}", execution.getStatus());
            } catch (Exception e) {
                logger.error("Error running batch job", e);
            }
        });
    }

}