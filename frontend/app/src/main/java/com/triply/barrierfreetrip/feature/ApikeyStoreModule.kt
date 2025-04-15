package com.triply.barrierfreetrip.feature

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class ApikeyStoreModule(private val context: Context) {
    private val Context.keyStore:
            DataStore<Preferences> by preferencesDataStore(name = "keyStore")
    private val apiKey = stringPreferencesKey("apikey")
    private val refreshTokenKey = stringPreferencesKey("encrypted_refresh_token")
    private val ivKey = stringPreferencesKey("encrypted_iv")

    val getApiKey: Flow<String> = context.keyStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }
        .map { value: Preferences -> value[apiKey] ?: "" }

    suspend fun setApiKey(key : String) {
        context.keyStore.edit { preferences ->
            preferences[apiKey] = key
        }
    }

    /**
     * @return 암호화된 Refresh Token을 복호화하여 리턴
     */
    fun getRefreshToken(): Flow<String> {
        return context.keyStore.data.map { prefs ->
            val (encryptedToken, iv) = Pair(prefs[refreshTokenKey] ?: "", prefs[ivKey] ?: "")
            EncryptionModule.decryptText(encryptedText = encryptedToken, keyAlias = REFRESH_TOKEN_KEY, ivBase64 = iv)
        }
    }
    
    /**
     * @param token 리프레시 토큰 그대로 저장 -> 내부적으로 암호화 후 저장
     */
    suspend fun setRefreshToken(token: String) {
        val (encryptedToken, iv) =
            EncryptionModule.encryptText(
                text = token,
                keyAlias = REFRESH_TOKEN_KEY
            )

        context.keyStore.edit { prefs ->
            prefs[refreshTokenKey] = encryptedToken
            prefs[ivKey] = iv
            println("ENCRYPTED TOKEN: $encryptedToken")
        }
    }

    companion object {
        const val REFRESH_TOKEN_KEY = "refresh_token_key"
    }
}