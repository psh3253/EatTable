package com.astar.eattable.config;

import com.astar.eattable.reservation.service.ReservationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReservationCommandService reservationCommandService;

    @Bean
    public Step createTableAvailabilitiesStep() {
        return new StepBuilder("createAllTableAvailabilityStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    reservationCommandService.createAllTableAvailabilities();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job createAllTableAvailabilitiesJob() {
        return new JobBuilder("createAllTableAvailabilitiesJob", jobRepository)
                .start(createTableAvailabilitiesStep())
                .build();
    }
}
