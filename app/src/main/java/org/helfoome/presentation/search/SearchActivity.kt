package org.helfoome.presentation.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivitySearchBinding
import org.helfoome.domain.entity.RestaurantInfo
import org.helfoome.presentation.search.adapter.*
import org.helfoome.presentation.search.type.SearchMode
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.closeKeyboard
import org.helfoome.util.ext.showKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BindingActivity<ActivitySearchBinding>(R.layout.activity_search) {
    // TODO : Inject 로직 수정 요망
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val viewModel: SearchViewModel by viewModels()
    private val autoCompleteAdapter = AutoCompleteAdapter {
        viewModel.setDetail(true)
        viewModel.setSearchMode(SearchMode.RESULT)
    }
    private val recentAdapter = RecentAdapter(
        {
            // TODO : 서버 통신 주의
            binding.etSearch.setText(it)
            viewModel.setSearchMode(SearchMode.RESULT)
        },
    ) {
        viewModel.removeKeyword(it)
    }
    private val resultAdapter = ResultAdapter {
        // TODO : 서버 통신 주의
        viewModel.setDetail(true)
    }

    private val searchMapTopAdapter = SearchMapTopAdapter {
        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = resolutionMetrics.toPixel(135)
            behavior.isDraggable = true
        }
    }
    private val searchRecentTopAdapter = SearchRecentTopAdapter()
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            with(newState != BottomSheetBehavior.STATE_EXPANDED) {
                if (viewModel.isDetail.value && !this)
                    binding.layoutSearch.visibility = View.GONE
                else
                    binding.layoutSearch.visibility = View.VISIBLE
                searchMapTopAdapter.setVisible(this)
                behavior.isDraggable = this
                binding.isLineVisible = this
            }
        }

        override fun onSlide(bottomSheetView: View, slideOffset: Float) {}
    }

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

        initView()
        openKeyboard()
        initClickEvent()
        initFocusChangeListener()
        initKeyListeners()
        initTextChangeEvent()
        observeData()
    }

    override fun onStart() {
        super.onStart()
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun openKeyboard() {
        binding.etSearch.requestFocus()
        showKeyboard(binding.etSearch)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(binding.layoutBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun initClickEvent() {
        with(binding) {
            with(viewModel) {
                btnBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }

                layoutRestaurantDetailDialog.btnBack.setOnClickListener {
                    startSearchModeBackEvent(searchMode.value)
                }
            }

            btnDelete.setOnClickListener {
                when (viewModel.searchMode.value) {
                    SearchMode.AUTO_COMPLETE -> etSearch.text.clear()
                    else -> finish()
                }
            }
        }
    }

    private fun initFocusChangeListener() {
        binding.etSearch.setOnFocusChangeListener { _, isFocus ->
            if (isFocus && viewModel.searchMode.value == SearchMode.RESULT) {
                viewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
                viewModel.setDetail(false)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun startSearchModeBackEvent(value: SearchMode) {
        when (value) {
            SearchMode.RESULT -> {
                if (viewModel.isDetail.value) {
                    if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        binding.isLineVisible = true
                        behavior.peekHeight = resolutionMetrics.toPixel(155)
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        viewModel.setDetail(false)
                        viewModel.setSearchMode(SearchMode.RESULT)
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                } else {
                    if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                        viewModel.setSearchMode(SearchMode.RESULT)
                    else
                        viewModel.setSearchMode(SearchMode.RECENT)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            else -> {
                finish()
            }
        }
    }

    private fun initTextChangeEvent() {
        with(binding.etSearch) {
            addTextChangedListener {
                if (it.isNullOrEmpty()) {
                    closeKeyboard(this)
                    this.clearFocus()
                    viewModel.setSearchMode(SearchMode.RECENT)
                } else {
                    viewModel.setSearchMode(SearchMode.AUTO_COMPLETE)
                    viewModel.getAutoCompleteKeyword(it.toString())
                }
            }
        }
    }

    private fun initKeyListeners() {
        with(binding.etSearch) {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                    // TODO : 최근 검색어 추가 서버 통신 시 수정
                    viewModel.getSearchResultCardList(37.498095, 127.027610, text.toString())
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
                            isDeleteButtonVisible = false
                            etSearch.text.clear()
                            layoutRestaurantListDialog.rvSearch.adapter = recentConcatAdapter
                        }
                        SearchMode.AUTO_COMPLETE -> {
                            // TODO : 자동완성 서버 통신
                            btnDelete.isSelected = false
                            isDeleteButtonVisible = true
                            layoutRestaurantListDialog.rvSearch.adapter = autoCompleteAdapter
                        }
                        else -> {
                            // TODO : 서버 통신 받아온 리스트 띄우기
                            binding.etSearch.clearFocus()
                            btnDelete.isSelected = true
                            isDeleteButtonVisible = true
                            layoutRestaurantListDialog.rvSearch.adapter = resultConcatAdapter
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
                            with(autoCompleteAdapter) {
                                setKeywordListener {
                                    it.data.first
                                }
                                autoCompleteAdapter.submitList(it.data.second)
                                // TODO : 추후 더 좋은 데이터 갱신 요망
                                autoCompleteAdapter.notifyDataSetChanged()
                            }
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

            viewModel.isDetail.flowWithLifecycle(lifecycle)
                .onEach {
                    when (it) {
                        true -> {
                            binding.isDetail = true
                            behavior.peekHeight = resolutionMetrics.toPixel(155)
                            behavior.isDraggable = true
//                            behavior.isHideable = true
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        else -> {
//                            behavior.isHideable = false
                            binding.isDetail = false
                        }
                    }
                }
                .launchIn(lifecycleScope)

            layoutRestaurantDetailDialog.restaurant = RestaurantInfo(
                id = "62d26c9bd11146a81ef18eb8",
                image = "https://salady.com/superboard/data/siteconfig/2021021809004816136064486235.jpg",
                name = "샐러디",
                score = 4.8f,
                tags = listOf("샐러드", "샌드위치", "랩"),
                category = "샐러드",
                location = "서울특별시 중랑구 상봉동",
                time = listOf(
                    "화요일 10:00 ~ 22:00",
                    "수요일 10:00 ~ 22:00",
                    "목요일 10:00 ~ 22:00",
                    "금요일 10:00 ~ 22:00",
                    "토요일 10:00 ~ 22:00",
                    "일요일 10:00 ~ 22:00",
                    "월요일 10:00 ~ 22:00"
                ),
                contact = "02-123-123"
            )
        }
    }

    override fun onStop() {
        super.onStop()
        behavior.removeBottomSheetCallback(bottomSheetCallback)
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
        // TODO : Collpased 높이 이상한거랑
        // TODO : hide 안되는 거
        startSearchModeBackEvent(viewModel.searchMode.value)
    }
}
