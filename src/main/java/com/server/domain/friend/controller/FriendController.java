package com.server.domain.friend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.server.domain.friend.dto.GetFriendOutDto;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.friend.service.FriendService;
import com.server.global.dto.ApiResponseDto;
import com.server.global.jwt.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final JwtService jwtService;
    private final FriendService friendService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/request")
    @Operation(summary = "친구 신청 생성", description = "친구 신청할 사용자의 이름을 입력하여 친구 신청 생성")
    public ApiResponseDto<String> createFriendRequest(@RequestParam String receiptUsername,
            HttpServletRequest request) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.createFriendRequest(requestUsername, receiptUsername);
        return ApiResponseDto.success(HttpStatus.CREATED.value(),
                String.format("A friend request from User '%s' to User '%s' has been created.",
                        requestUsername, receiptUsername));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/request")
    @Operation(summary = "친구 내역 조회", description = "사용자 기준 신청 보낸 내역")
    public ApiResponseDto<List<GetFriendOutDto>> getFriendRequest(
            @RequestParam(required = false) FriendState state,
            HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<GetFriendOutDto> getUserOutDtos = friendService.getRequestUser(username, state);
        return ApiResponseDto.success(HttpStatus.OK.value(), getUserOutDtos);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/request")
    @Operation(summary = "친구 신청 취소", description = "친구 신청할 사용자의 이름을 입력하여 친구 신청 삭제")
    public ApiResponseDto<String> deleteFriendRequest(@RequestParam String receiptUsername,
            HttpServletRequest request) {
        String requestUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.deleteFriendRequest(requestUsername, receiptUsername);
        return ApiResponseDto.success(HttpStatus.OK.value(), String
                .format("The friend request from '%s' to '%s' has been deleted.", requestUsername, receiptUsername));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/receipt")
    @Operation(summary = "친구 내역 조회", description = "사용자 기준 신청 받은 내역")
    public ApiResponseDto<List<GetFriendOutDto>> getFriendReceipt(
            @RequestParam(required = false) FriendState state,
            HttpServletRequest request) {
        String username = jwtService.extractUsernameFromToken(request).get();
        List<GetFriendOutDto> getUserOutDtos = friendService.getReceiptUser(username, state);
        return ApiResponseDto.success(HttpStatus.OK.value(), getUserOutDtos);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/receipt")
    @Operation(summary = "친구 신청 승인", description = "친구 신청을 승인할 사용자의 이름을 입력하여 친구 신청 승인")
    public ApiResponseDto<String> approveFriendRequest(@RequestParam String requestUsername,
            HttpServletRequest request) {
        String receiptUsername = jwtService.extractUsernameFromToken(request).get();
        friendService.acceptFriendRequest(requestUsername, receiptUsername);
        return ApiResponseDto.success(HttpStatus.CREATED.value(), String.format(
                "The friend request from User '%s' to User '%s' has been accepted.", requestUsername, receiptUsername));
    }
}
