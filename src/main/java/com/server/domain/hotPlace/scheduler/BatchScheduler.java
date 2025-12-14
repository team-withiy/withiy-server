package com.server.domain.hotPlace.scheduler;

import com.server.domain.hotPlace.service.ViewAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

    private final ViewAggregationService viewAggregationService;

//    테스트용
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void aggregateDailyViews() {
        viewAggregationService.aggregateLast7Days();
    }
}
