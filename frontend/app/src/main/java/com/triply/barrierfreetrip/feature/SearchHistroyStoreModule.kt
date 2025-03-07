package com.triply.barrierfreetrip.feature

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

private val Context.searchStore:
        DataStore<Preferences> by preferencesDataStore(name = "search_store")

class SearchHistoryStoreModule(private val context: Context) {
    private val searchKeywordList = stringPreferencesKey("search_list")

    fun getSearchHistory(): Flow<List<String>> {
        return context.searchStore.data
            .catch { exception ->
                if (exception is IOException)
                    emit(emptyPreferences())
                else
                    throw exception
            }
            .map { pref: Preferences ->
                val existingSearchKeywordMap =
                    Gson().fromJson(pref[searchKeywordList] ?: "{}", Map::class.java).toMutableMap()
                existingSearchKeywordMap.toList().sortedByDescending { it.second.toString() }.map { it.first.toString() }
            }
    }

    suspend fun updateSearchHistory(searchKeyword: String) {
        context.searchStore.edit { preferences ->
            val gson = Gson()
            // Json으로 변환되었던 datastore의 검색어 Json String으로 저장되었던 것을 맵(map)을 으로 변환
            val existingSearchKeywordMap =
                gson.fromJson(preferences[searchKeywordList] ?: "{}", Map::class.java).toMutableMap()

            // 검색어-검색시간 맵에 새 키워드에 대한 값 저장
            existingSearchKeywordMap[searchKeyword] = SimpleDateFormat("yyyy-MM-DD HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()).toString()

            // 만약 검색어가 10개가 넘어가면 가장 먼저 검색했던 것 삭제
            if (existingSearchKeywordMap.keys.size > 10) {
                val firstSearchKeyword = existingSearchKeywordMap.toList().minBy { it.second.toString() }.toString()
                existingSearchKeywordMap.remove(firstSearchKeyword)
            }

            // 다시 Json으로 변환하여 datastore에 저장
            preferences[searchKeywordList] = gson.toJson(existingSearchKeywordMap)
        }
    }

    suspend fun deleteSearchHistory(searchKeyword: String) {
        context.searchStore.edit { preferences ->
            val gson = Gson()
            // Json으로 변환되었던 datastore의 검색어 Json String으로 저장되었던 것을 맵(map)을 으로 변환
            val existingSearchKeywordMap =
                gson.fromJson(preferences[searchKeywordList] ?: "{}", Map::class.java).toMutableMap()

            // 검색어-검색시간 맵에서 키워드 삭제
            if (existingSearchKeywordMap.containsKey(searchKeyword)) existingSearchKeywordMap.remove(searchKeyword)

            // 다시 Json으로 변환하여 datastore에 저장
            preferences[searchKeywordList] = gson.toJson(existingSearchKeywordMap)
        }
    }

    suspend fun deleteAllSearchHistory() {
        context.searchStore.edit { preferences ->
            val gson = Gson()
            // Json으로 변환되었던 datastore의 검색어 Json String으로 저장되었던 것을 맵(map)을 으로 변환
            val existingSearchKeywordMap =
                gson.fromJson(preferences[searchKeywordList] ?: "{}", Map::class.java).toMutableMap()

            // 검색어-검색시간 맵에서 키워드 모두 삭제
            existingSearchKeywordMap.keys.toList().forEach { key ->
                existingSearchKeywordMap.remove(key)
            }

            // 다시 Json으로 변환하여 datastore에 저장
            preferences[searchKeywordList] = gson.toJson(existingSearchKeywordMap)
        }
    }
}