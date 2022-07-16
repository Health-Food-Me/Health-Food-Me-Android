package org.helfoome.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun setDetail(isDetail: Boolean) {
        _isDetail.value = isDetail
    }

    fun insertKeyword(keyword: String) {
        viewModelScope.launch {
            searchRepository.insertKeyword(
                RecentSearchInfo(keyword)
            )
        }
    }

    fun removeKeyword(keyword: String) {
        viewModelScope.launch {
            searchRepository.removeKeyword(
                RecentSearchInfo(keyword)
            )
        }
    }

    private fun List<RecentSearchInfo>.toUiState() = SearchUiState.RecentSearch(this)

    private fun List<AutoCompleteKeywordInfo>.toUiState() = SearchUiState.AutoCompleteSearch(this)

    private fun List<SearchResultInfo>.toUiState() = SearchUiState.Result(this)

    sealed class SearchUiState {
        object Loading : SearchUiState()
        data class RecentSearch(val data: List<RecentSearchInfo>) : SearchUiState()
        data class AutoCompleteSearch(val data: List<AutoCompleteKeywordInfo>) : SearchUiState()
        data class Result(val data: List<SearchResultInfo>) : SearchUiState()
        class Error(val message: String?) : SearchUiState()
    }

    companion object {
        const val SEARCH_RECENT = 0
        const val SEARCH_AUTO_COMPLETE = 1
        const val SEARCH_RESULT = 2
        const val DIET = 0
        const val NORMAL = 1
        const val LIST = false
        const val DETAIL = true
    }
}
