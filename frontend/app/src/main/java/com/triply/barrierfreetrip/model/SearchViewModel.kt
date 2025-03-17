package com.triply.barrierfreetrip.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.triply.barrierfreetrip.feature.SearchHistoryStoreModule
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchKeywordDatastore = SearchHistoryStoreModule(application)

    private val _searchKeywordList: MutableLiveData<List<String>?> by lazy { MutableLiveData(null) }
    val searchKeywordList: LiveData<List<String>?>
        get() = _searchKeywordList

    fun addSearchKeyword(keyword: String) {
        viewModelScope.launch {
            searchKeywordDatastore.updateSearchHistory(keyword.take(100))
        }
    }

    private fun getAllSearchKeywordList() {
        viewModelScope.launch {
            searchKeywordDatastore.getSearchHistory().collect {
                _searchKeywordList.value = it
            }
        }
    }

    fun deleteSearchKeyword(keyword: String) {
        viewModelScope.launch {
            searchKeywordDatastore.deleteSearchHistory(keyword)
        }
    }

    fun deleteAllSearchKeyword() {
        viewModelScope.launch {
            searchKeywordDatastore.deleteAllSearchHistory()
        }
    }

    init {
        getAllSearchKeywordList()
    }
}