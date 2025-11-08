package com.server.domain.dateSchedule.repository;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.user.entity.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DateSchedRepository extends JpaRepository<DateSchedule, Long> {

    @Query("SELECT ds FROM DateSchedule ds "
            + "WHERE ds.user = :user "
            + "AND TO_CHAR(ds.scheduleAt, 'YYYY-MM') = :yyyyMm ")
    List<DateSchedule> findByUserAndScheduleAtYyyyMm(
            @Param("user") User user,
            @Param("yyyyMm") String yyyyMm
    );

    List<DateSchedule> findByUserAndScheduleAt(User user, LocalDate scheduleAt);

    Optional<DateSchedule> findByUserAndId(User user, Long dateSchedId);

    Optional<DateSchedule> findByUserAndAlbum_Id(User user, Long albumId);

}
