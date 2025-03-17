package com.triply.barrierfreetrip.api

import com.triply.barrierfreetrip.data.LoginDto
import com.triply.barrierfreetrip.data.loginParameter
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("/login")
    suspend fun getToken(
        @Body loginParameter: loginParameter
    ) : Response<LoginDto>

    @GET("/authorize")
    suspend fun getTokenWithHTML(
        @Query(value = "client_id") cliendId : String,
        @Query(value = "redirect_uri") url : String,
        @Query(value = "response_type") type : String
    ) : Response<ResponseBody>
}