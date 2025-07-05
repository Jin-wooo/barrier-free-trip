package com.triply.barrierfreetrip.api

import com.triply.barrierfreetrip.data.LogoutDto
import com.triply.barrierfreetrip.feature.ApikeyStoreModule
import com.triply.barrierfreetrip.feature.LogoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val apikeyStoreModule: ApikeyStoreModule,
    private val loginApi: LoginApi
) : Authenticator {
    private var refreshJob: Deferred<String?>? = null

    // 액세스 토큰 만료로 인한 401 에러 시 발생
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val newToken = runBlocking {
            mutex.withLock {
                refreshJob?.let { return@runBlocking it.await() }

                refreshJob = CoroutineScope(Dispatchers.IO).async {
                    val refreshToken = apikeyStoreModule.getRefreshToken().firstOrNull()
                        ?: return@async null

                    try {
                        val refreshResponse = loginApi.refreshToken(LogoutDto(refreshToken))
                        if (!refreshResponse.isSuccessful) {
                            throw Exception(refreshResponse.errorBody()?.string())
                        }
                        val newAccessToken = refreshResponse.body()?.respDocument?.accessToken
                        if (newAccessToken.isNullOrEmpty()) {
                            throw Exception(refreshResponse.body()?.message)
                        }

                        try {
                            apikeyStoreModule.setAccessToken(newAccessToken)
                        } catch (_: Exception) {
                            // Ignore write error; fallback to memory cache if needed
                        }

                        newAccessToken
                    } catch (e: Exception) {
                        handleLogout()
                        null
                    }
                }
            }

            try {
                refreshJob?.await()
            } finally {
                mutex.withLock { refreshJob = null }
            }
        }
        return newToken?.let {
            response.request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }

    private suspend fun handleLogout(): Request? {
        LogoutManager.broadcastLogoutEvent()
        return null
    }

    companion object {
        private val mutex = Mutex()
    }
}