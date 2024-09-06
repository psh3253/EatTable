package com.astar.eattable.reservation.service;

import com.astar.eattable.reservation.command.ReservationCreateCommand;
import com.astar.eattable.reservation.model.TableAvailability;
import com.astar.eattable.reservation.repository.ReservationRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/concurrency-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReservationServiceConcurrencyTest {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceConcurrencyTest.class);

    @Autowired
    private ReservationCommandService reservationCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private TableAvailabilityRepository tableAvailabilityRepository;

    @Test
    @DisplayName("50명이 동시에 예약을 시도하면 25명은 성공하고 25명은 실패한다.")
    public void testConcurrentReservation_with50People_success25People_fail25People() throws InterruptedException {
        // given
        ReservationCreateCommand command = new ReservationCreateCommand(1L, "2024-09-01", "10:00", 2, "요청 사항");
        User user = userRepository.findById(1L).orElseThrow();

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    reservationCommandService.createReservation(command, user);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double averageTime = (double) duration / threadCount;

        // then
        TableAvailability tableAvailability = tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(1L, LocalDate.parse("2024-09-01"), LocalTime.parse("10:00"), 2).orElseThrow();
        assertThat(tableAvailability.getRemainingTableCount()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(25);
        assertThat(failCount.get()).isEqualTo(25);

        logger.info("Duration: {}", duration);
        logger.info("Average Time: {}", averageTime);
    }

    @Test
    @DisplayName("25명이 동시에 예약을 취소를 시도하면 모두 성공한다.")
    public void testConcurrentCancel_with25People_success25People() throws InterruptedException {
        // given
        User user = userRepository.findById(1L).orElseThrow();

        int threadCount = 25;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong reservationId = new AtomicLong(1L);

        long startTime = System.currentTimeMillis();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    reservationCommandService.cancelReservation(reservationId.getAndIncrement(), user);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        double averageTime = (double) duration / threadCount;

        // then
        TableAvailability tableAvailability = tableAvailabilityRepository.findByRestaurantIdAndDateAndStartTimeAndRestaurantTableCapacity(1L, LocalDate.parse("2024-09-01"), LocalTime.parse("10:00"), 3).orElseThrow();
        assertThat(tableAvailability.getRemainingTableCount()).isEqualTo(25);
        assertThat(successCount.get()).isEqualTo(25);
        assertThat(failCount.get()).isEqualTo(0);

        logger.info("Duration: {}", duration);
        logger.info("Average Time: {}", averageTime);
    }
}

