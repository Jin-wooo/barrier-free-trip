package com.triply.barrierfreetrip.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.member.domain.Token;
import com.triply.barrierfreetrip.member.dto.MemberRequestDto;
import com.triply.barrierfreetrip.member.dto.MemberResponseDto;
import com.triply.barrierfreetrip.member.service.OauthMemberService;
import com.triply.barrierfreetrip.member.service.RefreshTokenService;
import com.triply.barrierfreetrip.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final OauthMemberService oauthMemberService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @GetMapping("/welcome")
    public String healthCheck() {
        return "welcome";
    }


    @GetMapping("/oauth/kakao")
    public ApiResponseBody<?> kakaoLoin(@RequestParam("code") String code)
                                        throws JsonProcessingException {
        try {
            Member member = oauthMemberService.oauthLogin(code, "kakao");
            // generate jwt
            Token token = tokenService.generateToken(member.getEmail(), member.getRoles());

            // save refresh token
            refreshTokenService.saveRefreshToken(token);
//        MemberResponseDto memberResponseDto = new MemberResponseDto(member.getEmail(), member.getNickname(),
//                                                                    token.getAccessToken(), token.getRefreshToken());
            return ApiResponseBody.createSuccess(token);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/oauth/naver")
    public ApiResponseBody<?> naverLoin(@RequestParam("code") String code)
            throws JsonProcessingException {

        try {
            Member member = oauthMemberService.oauthLogin(code, "naver");
            // generate jwt
            Token token = tokenService.generateToken(member.getEmail(), member.getRoles());

            // save refresh token
            refreshTokenService.saveRefreshToken(token);
//        MemberResponseDto memberResponseDto = new MemberResponseDto(member.getEmail(), member.getNickname(),
//                                                                    token.getAccessToken(), token.getRefreshToken());
            return ApiResponseBody.createSuccess(token);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponseBody<?> login(@RequestBody MemberRequestDto memberRequestDto) {
        try {
            Member member = oauthMemberService.login(memberRequestDto);

            // generate jwt
            Token token = tokenService.generateToken(member.getEmail(), member.getRoles());

            // save refresh token
            refreshTokenService.saveRefreshToken(token);
            MemberResponseDto memberResponseDto = new MemberResponseDto(token.getAccessToken());

            return ApiResponseBody.createSuccess(memberResponseDto);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @PostMapping("/mylogout")
    public ApiResponseBody<?> logout(@RequestBody HashMap<String, String> bodyJson) {
        try {
            String refreshToken = bodyJson.get("refreshToken");

            // delete refresh token
            refreshTokenService.deleteRefreshToken(refreshToken);

            return ApiResponseBody.createSuccess(emptyDocument);

        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
