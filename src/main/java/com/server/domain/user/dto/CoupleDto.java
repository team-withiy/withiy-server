package com.server.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class CoupleDto {

	@Schema(description = "커플 고유 ID", example = "1")
	private Long id;

	@Schema(description = "파트너 닉네임", example = "파트너닉네임")
	private String partnerNickname;

	@Schema(description = "파트너 프로필 이미지", example = "https://example.com/profile.jpg")
	private String partnerThumbnail;

	@Schema(description = "복구 가능 여부", example = "true")
	private Boolean restoreEnabled;

	@Schema(description = "처음 만난 날짜", example = "2025-01-01")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate firstMetDate;

	public static CoupleDto from(Couple couple, User currentUser) {

		User partner = couple.getPartnerOf(currentUser);

		return CoupleDto.builder()
			.id(couple.getId())
			.partnerNickname(partner.getNickname())
			.partnerThumbnail(partner.getThumbnail())
			.restoreEnabled(couple.isRestorable())
			.firstMetDate(couple.getFirstMetDate())
			.build();
	}
}
