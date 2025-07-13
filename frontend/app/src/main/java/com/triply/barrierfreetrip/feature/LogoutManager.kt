package com.triply.barrierfreetrip.feature

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LogoutManager {
    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvent = _logoutEvent.asSharedFlow()

    suspend fun broadcastLogoutEvent() {
        _logoutEvent.emit(Unit)
    }
}