package com.triply.barrierfreetrip.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LocationInstance {
    private val BASE_URL = "https://dapi.kakao.com/v2/local/search/"

    fun getLocationApi() : LocationApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(LocationApi::class.java)
    }
}