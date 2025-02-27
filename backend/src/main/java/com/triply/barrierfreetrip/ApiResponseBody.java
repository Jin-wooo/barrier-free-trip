package com.triply.barrierfreetrip;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ApiResponseBody<T> {
    private String status;
    private T data;
    private String message;

    public static <T> ApiResponseBody<T> createSuccess(T data) {
        return new ApiResponseBody<>("success", data, "");
    }

    public static <T> ApiResponseBody<T> createFail(T data, String message) {
        return new ApiResponseBody<>("error", data, message);
    }
}
