package com.server.domain.review.controller;

import com.server.domain.review.dto.CreateReviewRequest;
import com.server.domain.review.service.ReviewFacade;
import com.server.domain.user.entity.User;
import com.server.global.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/reviews")
public class ReviewController {

	private final ReviewFacade reviewFacade;

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponseDto<String> createReview(@AuthenticationPrincipal User user,
		@RequestBody CreateReviewRequest request) {

		reviewFacade.createReview(request.getPlaceId(), user, request.getContents(),
			request.getScore());

		return ApiResponseDto.success(HttpStatus.OK.value(), "리뷰 생성 성공");

	}

}
