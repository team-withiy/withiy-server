package com.server.domain.dateSchedule.service;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.dateSchedule.repository.DateSchedRepository;
import com.server.domain.user.entity.User;
import com.server.global.error.code.AlbumErrorCode;
import com.server.global.error.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DateSchedService {
    private final DateSchedRepository dateSchedRepository;

    public DateSchedule save(DateSchedule dateSchedule) {
        return dateSchedRepository.save(dateSchedule);
    }

    public List<DateSchedule> findByScheduleAtYyyyMm(User user, String yyyyMm) {
        return dateSchedRepository.findByUserAndScheduleAtYyyyMm(user, yyyyMm);
    }

    public List<DateSchedule> findByScheduleAtYyyyMmDd(User user, String yyyyMmDd) {
        return dateSchedRepository.findByUserAndScheduleAt(user, LocalDate.parse(yyyyMmDd));
    }

    public DateSchedule findByUserAndId(User user, Long dateSchedId) {
        return dateSchedRepository.findByUserAndId(user, dateSchedId);
    }

    public DateSchedule findByUserAndAlbum_Id(User user, Long albumId) {
        return dateSchedRepository.findByUserAndAlbum_Id(user, albumId)
                .orElseThrow(() -> new BusinessException(AlbumErrorCode.NOT_FOUND_ALBUM_FOR_SCHEDULE));
    }
}
