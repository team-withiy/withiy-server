package com.server.domain.dateSchedule.dto;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedUpdateRequest {
    @Valid
    private List<DateSchedUpdatePlaceDto> places;
}
