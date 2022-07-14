package org.helfoome.presentation.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivitySearchBinding
import org.helfoome.presentation.search.adapter.*
import org.helfoome.presentation.search.type.SearchMode
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import org.helfoome.util.ext.showKeyboard

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {
    private val viewModel: SearchViewModel by viewModels()
    private val autoCompleteAdapter = AutoCompleteAdapter()
    private val recentAdapter = RecentAdapter {
        viewModel.removeKeyword(it)
    }
    private val resultAdapter = ResultAdapter()
    private val searchMapTopAdapter = SearchMapTopAdapter()
    private val searchRecentTopAdapter = SearchRecentTopAdapter()

    private val recentConcatAdapter = ConcatAdapter(
        searchRecentTopAdapter,
        recentAdapter
    )

    private val resultConcatAdapter = ConcatAdapter(
        searchMapTopAdapter,
        resultAdapter
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openKeyboard()
        initClickEvent()
        initKeyListeners()
        initTextChangeEvent()
        observeData()
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        showKeyboard(binding.etSearch)
    }

    private fun initClickEvent() {
        with(binding) {
            with(viewModel) {
                btnBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }

                etSearch.setOnClickListener {
                    if (searchMode.value == SearchMode.RESULT) {
                        setSearchMode(SearchMode.AUTO_COMPLETE)
                    }
                }
            }

            btnDelete.setOnClickListener {
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
            if (it.isNullOrEmpty())
                viewModel.setSearchMode(SearchMode.RECENT)
            else
                viewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
        }
    }

    private fun initKeyListeners() {
        with(binding.etSearch) {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    // TODO : 최근 검색어 추가 서버 통신 시 수정
                    viewModel.insertKeyword(text.toString())
                    closeKeyboard(this)
                    binding.etSearch.clearFocus()
                    viewModel.setSearchMode(SearchMode.RESULT)
                }
                false
            }
        }
    }

    private fun observeData() {
        with(binding) {
            viewModel.searchMode.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        SearchMode.RECENT -> {
                            rvSearch.adapter = recentConcatAdapter
                        }
                        SearchMode.AUTO_COMPLETE -> {
                            // TODO : 자동완성 서버 통신
                            rvSearch.adapter = autoCompleteAdapter
                        }
                        else -> {
                            // TODO : 서버 통신 받아온 리스트 띄우기
                            rvSearch.adapter = resultConcatAdapter
                            closeKeyboard(binding.etSearch)
                        }
                    }
                }.launchIn(lifecycleScope)

            viewModel.searchUiState.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        is SearchViewModel.SearchUiState.RecentSearch -> {
                            recentAdapter.submitList(it.data)
                        }
                        is SearchViewModel.SearchUiState.AutoCompleteSearch -> {
                            autoCompleteAdapter.submitList(it.data)
                        }
                        is SearchViewModel.SearchUiState.Result -> {
                            resultAdapter.submitList(it.data)
                        }
                        else -> {
                            // TODO : Loading, Error 추후 구현
                        }
                    }
                }.launchIn(lifecycleScope)

            // 최근 검색어 받아오기
            viewModel.keywordList.flowWithLifecycle(lifecycle)
                .onEach {
                    recentAdapter.submitList(it)
                }
                .launchIn(lifecycleScope)
        }
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
