package com.server.domain.dateSchedule.dto;

import com.server.global.validation.DateFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateSchedCreateRequest {

    @Size(min = 1, max = 20)
    @NotEmpty(message = "name : 데이트명은 필수 입니다.")
    @Schema(description = "일정 명", example = "데이트 일정")
    private String name;

    @DateFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "날짜는 필수 입니다.")
    @Schema(description = "일정 날짜", example = "2025-12-25")
    private String scheduleAt;

    @Valid
    @NotEmpty(message = "places : 장소 정보는 필수 입니다.")
    private List<DateSchedPlaceDto> places;
}
