package com.triply.barrierfreetrip.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.triply.barrierfreetrip.BFTApplication
import com.triply.barrierfreetrip.api.RetroInstance
import com.triply.barrierfreetrip.data.LogoutDto
import com.triply.barrierfreetrip.util.Event
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val bftRetrofit = RetroInstance.createBFTApi("token")
    private val apiKeyStoreModule = (app as BFTApplication).getKeyStore()

    private val _isDataLoading by lazy { MutableLiveData(Event(false)) }
    val isDataLoading: LiveData<Event<Boolean>>
        get() = _isDataLoading

    private val _logoutResult by lazy { MutableLiveData(false) }
    val logoutResult: LiveData<Boolean>
        get() = _logoutResult

    private var refreshToken = ""

    fun logout() {
        viewModelScope.launch {
            try {
                _isDataLoading.value = Event(true)

                val response = bftRetrofit.logout(LogoutDto(refreshToken = refreshToken))
                if (response.isSuccessful) {
                    _logoutResult.value = response.body()?.status == "success"
                } else {
                    _logoutResult.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isDataLoading.value = Event(false)
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