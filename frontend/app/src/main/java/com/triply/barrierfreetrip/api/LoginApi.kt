package com.triply.barrierfreetrip.api

import com.triply.barrierfreetrip.data.LoginDto
import com.triply.barrierfreetrip.data.loginParameter
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApi {
    @POST("/login")
    suspend fun getToken(
        @Body loginParameter: loginParameter
    ) : Response<LoginDto>
}