package org.helfoome.presentation.search

import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivitySearchBinding
import org.helfoome.presentation.search.type.SearchMode
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClickEvent()
        initTextChangeEvent()
        observeData()
    }

    private fun initClickEvent() {
        with(binding) {
            with(viewModel) {
                ibBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }
            }

            ibDelete.setOnClickListener {
                etSearch.text.clear()
            }
        }
    }

    private fun startSearchModeBackEvent(value: SearchMode) {
        when (value) {
            SearchMode.RECENT -> {
                finish()
            }
            SearchMode.AUTO_COMPLETE -> {
                viewModel.setSearchMode(SearchMode.RECENT)
            }
            else -> {
                viewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
            }
        }
    }

    private fun initTextChangeEvent() {
        binding.etSearch.addTextChangedListener {
            if(it.isNullOrEmpty())
                viewModel.setSearchMode(SearchMode.RECENT)
            else
                viewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
        }
    }

    private fun observeData() {
        viewModel.searchMode.flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    SearchMode.RECENT -> {
                        // TODO : 로컬데이터 불러오기
                        binding.etSearch.isEnabled = true
                    }
                    SearchMode.AUTO_COMPLETE -> {
                        // TODO : 자동완성 서버 통신
                        binding.etSearch.isEnabled = true
                    }
                    else -> {
                        // TODO : 서버 통신 받아온 리스트 띄우기
                        binding.etSearch.isEnabled = false
                    }
                }
            }.launchIn(lifecycleScope)

        viewModel.searchUiState.flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    is SearchViewModel.SearchUiState.RecentSearch -> {

                    }
                    is SearchViewModel.SearchUiState.AutoCompleteSearch -> {

                    }
                    is SearchViewModel.SearchUiState.Result ->
                        Toast.makeText(this, "알 수 없는 에러", Toast.LENGTH_SHORT).show()
                    else -> {
                        // TODO : Loading, Error 추후 구현
                    }
                }
            }.launchIn(lifecycleScope)

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus

        if (view != null && ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val locationList = IntArray(2)
            view.getLocationOnScreen(locationList)
            val x = ev.rawX + view.left - locationList[0]
            val y = ev.rawY + view.top - locationList[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom) {
                closeKeyboard(view)
                view.clearFocus()
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        startSearchModeBackEvent(viewModel.searchMode.value)
    }
}
