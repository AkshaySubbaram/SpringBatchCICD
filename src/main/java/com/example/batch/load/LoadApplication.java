package com.example.batch.load;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
public class LoadApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(LoadApplication.class);

	private final JobLauncher jobLauncher;
	private final Job importSkillsJob;
	private final Job importMsTaskJob;
	private final Job importJob;
	private final Job updateTnUploadJob;

	private final Map<String, Job> jobs = new HashMap<>();

	@Autowired
	public LoadApplication(JobLauncher jobLauncher,
						   @Qualifier("importSkillsJob") Job importSkillsJob,
						   @Qualifier("importMsTaskJob") Job importMsTaskJob,
						   @Qualifier("importJob") Job importJob,
						   @Qualifier("updateTnUploadJob") Job updateTnUploadJob) {
		this.jobLauncher = jobLauncher;
		this.importSkillsJob = importSkillsJob;
		this.importMsTaskJob = importMsTaskJob;
		this.importJob = importJob;
		this.updateTnUploadJob = updateTnUploadJob;

		jobs.put("importskillsjob", importSkillsJob);
		jobs.put("importmstaskjob", importMsTaskJob);
		jobs.put("importjob", importJob);
		jobs.put("updatetnuploadjob", updateTnUploadJob);
	}


	public static void main(String[] args) {
		SpringApplication.run(LoadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			if (args.length > 0) {
				runSelectedJobs(args);
			} else {
				runAllJobs();
			}
		} catch (Exception e) {
			logger.error("Failed to execute jobs", e);
			throw e;
		}
	}

	private void runSelectedJobs(String[] jobNames) throws Exception {
		for (String jobName : jobNames) {
			Job job = jobs.get(jobName.toLowerCase());
			if (job != null) {
				runJob(job, jobName.toUpperCase());
			} else {
				logger.warn("Unknown job: {}", jobName);
			}
		}
	}

	private void runAllJobs() throws Exception {
		for (Map.Entry<String, Job> entry : jobs.entrySet()) {
			runJob(entry.getValue(), entry.getKey().toUpperCase());
		}
	}

	private void runJob(Job job, String jobIdentifier) throws Exception {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.addString("jobId", jobIdentifier + "_" + UUID.randomUUID())
					.toJobParameters();

			logger.info("Starting job: {}", job.getName());
			JobExecution execution = jobLauncher.run(job, jobParameters);

			if (execution.getStatus() == BatchStatus.FAILED) {
				logger.error("Job {} failed with exit status: {}",
						job.getName(), execution.getExitStatus());
			} else {
				logger.info("Job {} completed with status: {}",
						job.getName(), execution.getStatus());
			}
		} catch (Exception e) {
			logger.error("Failed to execute job: {}", job.getName(), e);
			throw e;
		}
	}

}
