package com.server.domain.hotPlace.service;

import com.server.domain.hotPlace.repository.ContentViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewAggregationService {

    private final ContentViewRepository contentViewRepository;

    @Transactional
    public void aggregateLast7Days() {
        log.info("[ViewAggregation] Start daily aggregation...");

        contentViewRepository.upsertDailyViewCounts();

        log.info("[ViewAggregation] Done ");
    }

}
