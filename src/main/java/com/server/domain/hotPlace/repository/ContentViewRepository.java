package com.server.domain.hotPlace.repository;

import com.server.domain.hotPlace.entity.ContentViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentViewRepository extends JpaRepository<ContentViewLog, Long> {

    @Modifying
    @Query(value = "INSERT INTO content_view_daily (type, content_id, view_count, reg_date) "
            + "SELECT "
            + "    c.type, "
            + "    c.content_id, "
            + "    CAST(SUM(CASE "
            // 어제 날짜인 경우 1.5 가중치 (reg_date 필터링된 범위 내에서)
            + "        WHEN c.reg_date >= NOW() - INTERVAL '24 HOUR' THEN 1.5 "
            + "        ELSE 1.0 " // 그 외 (7일 범위 내의 다른 날짜)는 1.0 가중치
            + "    END) AS BIGINT) AS view_count, "
            + "    NOW() AS reg_date "
            + "FROM content_view_log c "
            + "WHERE c.reg_date >= CURRENT_DATE - INTERVAL '7 DAY' "
            + "GROUP BY c.type, c.content_id "
            + "ON CONFLICT (type, content_id) "
            + "DO UPDATE SET "
            + "    view_count = EXCLUDED.view_count, "
            + "    reg_date = EXCLUDED.reg_date",
            nativeQuery = true)
    int upsertDailyViewCounts();
}
