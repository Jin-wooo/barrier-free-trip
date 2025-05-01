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
    private val accessTokenKey = stringPreferencesKey("encrypted_access_token")
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
     * 암호화하여 저장된 액세스 토큰값을 복호화하여 Flow 형태로 제공
     * @return 액세스 토큰값(Flow type)
     */
    fun getAccessToken(): Flow<String> {
        return context.keyStore.data.map { prefs ->
            val (encryptedToken, iv) = Pair(prefs[accessTokenKey] ?: "", prefs[ivKey] ?: "")
            EncryptionModule.decryptText(encryptedText = encryptedToken, keyAlias = ACCESS_TOKEN_KEY, ivBase64 = iv)
        }
    }

    /**
     * 액세스 토큰값을 암호화하여 데이터스토어에 저장
     * @param token 저장하고자 하는 액세스 토큰값 
     */
    suspend fun setAccessToken(token: String) {
        val (encryptedToken, iv) =
            EncryptionModule.encryptText(
                text = token,
                keyAlias = ACCESS_TOKEN_KEY
            )

        context.keyStore.edit { prefs ->
            prefs[accessTokenKey] = encryptedToken
            prefs[ivKey] = iv
        }
    }

    /**
     * 암호화하여 저장된 리프레시 토큰값을 복호화하여 Flow 형태로 제공
     * @return 리프레시 토큰값(Flow type)
     */
    fun getRefreshToken(): Flow<String> {
        return context.keyStore.data.map { prefs ->
            val (encryptedToken, iv) = Pair(prefs[refreshTokenKey] ?: "", prefs[ivKey] ?: "")
            EncryptionModule.decryptText(encryptedText = encryptedToken, keyAlias = REFRESH_TOKEN_KEY, ivBase64 = iv)
        }
    }
    
    /**
     * 리프레시 토큰값을 암호화하여 데이터스토어에 저장
     * @param token 저장하고자 하는 리프레시 토큰값
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
        }
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "access_token_key"
        const val REFRESH_TOKEN_KEY = "refresh_token_key"
    }
}