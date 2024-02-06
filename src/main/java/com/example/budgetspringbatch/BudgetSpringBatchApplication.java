package com.example.budgetspringbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BudgetSpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetSpringBatchApplication.class, args);
    }

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job readFlatFileJob; // Ensure this matches the bean name of your Job

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(readFlatFileJob, jobParameters);
            System.out.println("Job Status : " + jobExecution.getStatus());
        };
    }

}
