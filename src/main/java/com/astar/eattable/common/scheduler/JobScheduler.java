package com.astar.eattable.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobScheduler {
    private final JobLauncher jobLauncher;
    private final Job createAllTableAvailabilitiesJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void runCreateAllTableAvailabilitiesJob() {
        try {
            log.info("테이블 가용성 생성 배치를 실행합니다.");
            jobLauncher.run(createAllTableAvailabilitiesJob, new JobParameters());
            log.info("테이블 가용성 생성 배치를 성공적으로 실행했습니다.");
        } catch (Exception e) {
            log.error("테이블 가용성 생성 배치 실행 중 오류가 발생했습니다.", e);
        }
    }
}
