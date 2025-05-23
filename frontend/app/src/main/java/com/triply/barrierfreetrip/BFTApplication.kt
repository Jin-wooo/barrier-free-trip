package com.triply.barrierfreetrip

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import com.triply.barrierfreetrip.feature.ApikeyStoreModule
import com.triply.barrierfreetrip.feature.EncryptionModule

class BFTApplication : Application() {
    private lateinit var keyStore: ApikeyStoreModule

    companion object {
        private lateinit var instance : BFTApplication
        fun getInstance() : BFTApplication = instance
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        keyStore = ApikeyStoreModule(this)

        EncryptionModule.generateSecretKey(ApikeyStoreModule.REFRESH_TOKEN_KEY)

        val appInfo = applicationContext().applicationContext.packageManager.getApplicationInfo(
            applicationContext().applicationContext.packageName, PackageManager.GET_META_DATA
        )

        KakaoSdk.init(this, appInfo.metaData.getString("KAKAO_KEY").toString())
        KakaoMapSdk.init(this, BuildConfig.kakaomap_key)
    }

    fun getKeyStore(): ApikeyStoreModule = keyStore
}