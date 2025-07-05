package com.triply.barrierfreetrip.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.triply.barrierfreetrip.BFTApplication
import com.triply.barrierfreetrip.api.RetroInstance
import com.triply.barrierfreetrip.data.LogoutDto
import com.triply.barrierfreetrip.feature.LogoutManager
import com.triply.barrierfreetrip.util.Event
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val apiKeyStoreModule = BFTApplication.getInstance().getKeyStore()
    private var bftRetrofit = RetroInstance.bftAPI

    private val _isDataLoading by lazy { MutableLiveData(Event(false)) }
    val isDataLoading: LiveData<Event<Boolean>>
        get() = _isDataLoading

    private var refreshToken = ""

    fun logout() {
        UserApiClient.instance.logout { _ ->
            viewModelScope.launch {
                _isDataLoading.value = Event(true)

                try {
                    val response = bftRetrofit.logout(LogoutDto(refreshToken = refreshToken))
                    if (response.isSuccessful) {
                        LogoutManager.broadcastLogoutEvent()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isDataLoading.value = Event(false)
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            apiKeyStoreModule.getRefreshToken().collect {
                refreshToken = it
            }
        }
    }
}