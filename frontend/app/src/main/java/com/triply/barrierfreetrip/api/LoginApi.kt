package com.triply.barrierfreetrip.api

import com.triply.barrierfreetrip.data.LoginDto
import com.triply.barrierfreetrip.data.LoginParameter
import com.triply.barrierfreetrip.data.LogoutDto
import com.triply.barrierfreetrip.data.MetaResponse
import com.triply.barrierfreetrip.data.RefreshResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("/login")
    suspend fun getToken(
        @Body loginParameter: LoginParameter
    ) : Response<MetaResponse<LoginDto>>

    @POST("/refresh") // 어느 토큰이건 암튼 재발급! 에 가까운듯
    suspend fun refreshToken(
        @Body refreshToken: LogoutDto
    ) : Response<MetaResponse<RefreshResponse>>



    @GET("authorize")
    suspend fun getTokenWithHTML(
        @Query(value = "client_id") clientId : String,
        @Query(value = "redirect_uri") url : String,
        @Query(value = "response_type") type : String
    ) : Response<ResponseBody>
}