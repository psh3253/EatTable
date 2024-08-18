package com.astar.eattable.config;

import com.astar.eattable.reservation.service.ReservationCommandService;
import com.astar.eattable.reservation.service.ReservationQueryService;
import com.astar.eattable.restaurant.service.RestaurantCommandService;
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

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RestaurantCommandService restaurantCommandService;
    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    @Bean
    public Step createAllTableAvailabilitiesStep() {
        return new StepBuilder("createAllTableAvailabilityStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    reservationCommandService.createAllTableAvailabilities();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step createAllMonthlyAvailabilitiesStep() {
        return new StepBuilder("createAllMonthlyAvailabilitiesStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<Long> restaurantIds = restaurantCommandService.getAllRestaurantIds();
                    reservationQueryService.initAllMonthlyAvailabilities(restaurantIds);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job createAvailabilitiesJob() {
        return new JobBuilder("createAvailabilitiesJob", jobRepository)
                .start(createAllTableAvailabilitiesStep())
                .next(createAllMonthlyAvailabilitiesStep())
                .build();
    }
}
