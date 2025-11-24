package com.server.domain.hotPlace.service;

import com.server.domain.hotPlace.entity.ContentViewLog;
import com.server.domain.hotPlace.repository.ContentViewRepository;
import com.server.domain.route.entity.RouteType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentViewLogService {

    private final ContentViewRepository contentViewRepository;

    public void insertContentLog(RouteType routeType, Long id) {
        ContentViewLog contentViewLog = ContentViewLog.builder()
                .type(routeType)
                .contentId(id)
                .regDate(LocalDateTime.now())
                .build();
        contentViewRepository.save(contentViewLog);
    }

}
