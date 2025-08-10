package com.server.domain.user.dto;

import com.server.domain.user.entity.Couple;
import com.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestorableCoupleDto {

	@Schema(description = "커플 고유 ID", example = "1")
	private Long id;
	@Schema(description = "파트너 닉네임", example = "파트너닉네임")
	private String partnerNickname;

	@Schema(description = "파트너 프로필 이미지", example = "https://example.com/profile.jpg")
	private String partnerThumbnail;

	public static RestorableCoupleDto from(Couple couple, User partner) {
		return RestorableCoupleDto.builder()
			.id(couple.getId())
			.partnerNickname(partner.getNickname())
			.partnerThumbnail(partner.getThumbnail())
			.build();
	}
}
