package com.triply.barrierfreetrip.api

import android.content.pm.PackageManager
import com.triply.barrierfreetrip.BFTApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginInstance {
    private val appInfo = BFTApplication.applicationContext().applicationContext.packageManager.getApplicationInfo(
        BFTApplication.applicationContext().applicationContext.packageName, PackageManager.GET_META_DATA
    )
    val URL = RetroInstance.BASE_URL
    // API keys for social media login
    val KAKAO_KEY = appInfo.metaData.getString("KAKAO_KEY").toString()
    val NAVER_KEY = appInfo.metaData.getString("NAVER_KEY").toString()

    private val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getLoginApi() : LoginApi {
        return retrofit.create(LoginApi::class.java)
    }
}

