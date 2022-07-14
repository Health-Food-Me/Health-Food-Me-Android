package org.helfoome.presentation.search.type

import org.helfoome.presentation.search.SearchViewModel.Companion.SEARCH_AUTO_COMPLETE
import org.helfoome.presentation.search.SearchViewModel.Companion.SEARCH_RECENT
import org.helfoome.presentation.search.SearchViewModel.Companion.SEARCH_RESULT

enum class SearchMode(val id: Int, val visible: Boolean) {
    RECENT(SEARCH_RECENT, false),
    AUTO_COMPLETE(SEARCH_AUTO_COMPLETE, true),
    RESULT(SEARCH_RESULT, false)
}
