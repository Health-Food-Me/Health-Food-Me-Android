package org.helfoome.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.helfoome.domain.entity.AutoCompleteKeywordInfo
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.domain.entity.SearchResultInfo
import org.helfoome.domain.repository.SearchRepository
import org.helfoome.presentation.search.type.SearchMode
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    val keywordList: StateFlow<List<RecentSearchInfo>> =
        searchRepository.getRecentKeyword().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _searchUiState =
        MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val searchUiState: StateFlow<SearchUiState>
        get() = _searchUiState.asStateFlow()

    private val _searchMode =
        MutableStateFlow(SearchMode.RECENT)
    val searchMode: StateFlow<SearchMode>
        get() = _searchMode.asStateFlow()

    private val _isDetail =
        MutableStateFlow(LIST)
    val isDetail: StateFlow<Boolean>
        get() = _isDetail.asStateFlow()

    fun setSearchMode(searchMode: SearchMode) {
        _searchMode.value = searchMode
    }

    fun getAutoCompleteKeyword(query: String) {
        viewModelScope.launch {
            delay(150L)
            searchRepository.getSearchAutoComplete(127.027610, 37.498095, query)
                .onSuccess {
                    _searchUiState.value = it.toUiState(query)
                }
                .onFailure {
                    _searchUiState.value = SearchUiState.Error(it.message)
                }
        }
    }

    fun getSearchResultCardList(
        longtitude: Double,
        latitude: Double,
        keyword: String
    ) {
        viewModelScope.launch {
            searchRepository.getSearchRestaurantCard(longtitude, latitude, keyword)
                .onSuccess {
                    _searchUiState.value = it.toUiState(false)
                }
                .onFailure {
                    _searchUiState.value = SearchUiState.Error(it.message)
                }
        }
    }

    fun getSearchCategoryCardList(
        longitude: Double,
        latitude: Double,
        keyword: String
    ) {
        viewModelScope.launch {
            searchRepository.getSearchCategoryCard(longitude, latitude, keyword)
                .onSuccess {
                    _searchUiState.value = it.toUiState(true)
                }
                .onFailure {
                    _searchUiState.value = SearchUiState.Error(it.message)
                }
        }
    }

    fun setDetail(isDetail: Boolean) {
        _isDetail.value = isDetail
    }

    fun insertKeyword(keyword: String, isCategory: Boolean) {
        viewModelScope.launch {
            searchRepository.insertKeyword(
                RecentSearchInfo(keyword, isCategory)
            )
        }
    }

    fun removeKeyword(keyword: String) {
        viewModelScope.launch {
            searchRepository.removeKeyword(
                keyword
            )
        }
    }

    private fun List<RecentSearchInfo>.toUiState() = SearchUiState.RecentSearch(this)

    private fun List<AutoCompleteKeywordInfo>.toUiState(keyword: String) = SearchUiState.AutoCompleteSearch(Pair(keyword, this))

    private fun List<SearchResultInfo>.toUiState(isCategory: Boolean) = SearchUiState.Result(isCategory, this)

    sealed class SearchUiState {
        object Loading : SearchUiState()
        data class RecentSearch(val data: List<RecentSearchInfo>) : SearchUiState()
        data class AutoCompleteSearch(val data: Pair<String, List<AutoCompleteKeywordInfo>>) : SearchUiState()
        data class Result(val isCategory: Boolean, val data: List<SearchResultInfo>) : SearchUiState()
        class Error(val message: String?) : SearchUiState()
    }

    companion object {
        const val SEARCH_RECENT = 0
        const val SEARCH_AUTO_COMPLETE = 1
        const val SEARCH_RESULT = 2
        const val DIET = true
        const val NORMAL = false
        const val LIST = false
        const val DETAIL = true
    }
}
