package com.triply.barrierfreetrip.api

import com.google.gson.GsonBuilder
import com.triply.barrierfreetrip.data.MetaResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    val BASE_URL = "http://IP주소:8080/"

    private val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder()
                .registerTypeAdapter(MetaResponse::class.java, RetrofitDeserializer())
                .create()
        ))
    private var client = builder.build()

    fun getInstance() : Retrofit = client
    fun createBFTApi(token : String) : BFTApi {
        val interceptor = AuthInterceptor(token)

        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor)
            builder.client(httpClient.build())
            client = builder.build()
        }
        return client.create(BFTApi::class.java)
    }
}

