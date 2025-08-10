package com.server.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoupleConnectionRequestDto {

	@Schema(description = "상대방 유저 고유 코드", example = "ABC123")
	private String partnerCode;

	@Schema(description = "설정할 처음 만난 날짜", example = "2025-01-01")
	@JsonFormat(pattern = "yyyy-MM-dd")
	@PastOrPresent(message = "처음 만난 날은 오늘 이전이어야 합니다.")
	private LocalDate firstMetDate;
}
