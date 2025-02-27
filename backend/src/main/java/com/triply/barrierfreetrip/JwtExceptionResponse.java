package com.triply.barrierfreetrip;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class JwtExceptionResponse {
//    private String exceptionMessage;
//    private HttpStatus status;
    private String errorCode;

    /* 공통 응답 메시지 포맷 변경으로 인한 주석 처리
    public String convertToJson(JwtExceptionResponse jwtExceptionResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(jwtExceptionResponse);

        return json;
    }
     */
}
