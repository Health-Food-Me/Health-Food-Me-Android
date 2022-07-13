package org.helfoome.presentation.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.helfoome.domain.entity.AutoCompleteKeywordInfo
import org.helfoome.domain.entity.RecentSearchInfo
import org.helfoome.domain.entity.SearchResultInfo
import org.helfoome.presentation.search.type.SearchMode
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _searchUiState =
        MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val searchUiState: StateFlow<SearchUiState>
        get() = _searchUiState.asStateFlow()

    private val _searchMode =
        MutableStateFlow(SearchMode.RECENT)
    val searchMode: StateFlow<SearchMode>
        get() = _searchMode.asStateFlow()

    fun setSearchMode(searchMode: SearchMode) {
        _searchMode.value = searchMode
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
    }
}
