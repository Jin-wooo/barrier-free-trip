package com.triply.barrierfreetrip.api

import com.triply.barrierfreetrip.BuildConfig
import com.triply.barrierfreetrip.data.LocationCoordinateDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LocationApi {
    @GET("address.json")
    suspend fun getLocationCoordinate(
        @Header(value = "Authorization") apiKey: String = "KakaoAK ${BuildConfig.kakao_client_id}",
        @Query(value = "query") address: String
    ): Response<LocationCoordinateDTO>
}