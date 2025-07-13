package com.triply.barrierfreetrip.member.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @PostMapping("/refresh")
    public ApiResponseBody<?> verifyRefreshToken(@RequestBody HashMap<String, String> bodyJson) {
        try {
            String newAccessToken = refreshTokenService.verifyRefreshToken(bodyJson.get("refreshToken"));
            Map<String, String> map = new HashMap<>();

            // if uneffective refreshtoken
            if(newAccessToken == null) {
                map.put("accessToken", "");
                map.put("dMessage", "Refresh 토큰이 유효하지 않습니다. 로그인이 필요합니다.");
            } else {
                // if effective refreshtoken
                map.put("accessToken", newAccessToken);
                map.put("dMessage", "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다.");
            }

            return ApiResponseBody.createSuccess(map);

        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }

    }
}
