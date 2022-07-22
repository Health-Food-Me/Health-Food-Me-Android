package org.helfoome.presentation.scrap

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.helfoome.R
import org.helfoome.databinding.ActivityMyScrapBinding
import org.helfoome.presentation.scrap.adapter.MyScrapAdapter
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.startActivity

@AndroidEntryPoint
class MyScrapActivity : BindingActivity<ActivityMyScrapBinding>(R.layout.activity_my_scrap) {
    private val viewModel: ScrapViewModel by viewModels()
    private val myScrapAdapter = MyScrapAdapter(
        {
            // TODO : 다음 액티비티에서 받아서 그려줌
            // startActivity<MapSelectActivity>(Pair("RESTAURANT_ID", it))
        }
    ) {
        viewModel.putScrap(it)
    }

    // TODO : 서버 통신 연동 필요
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getScrapList()
        initAdapter()
        with(binding) {
            toolbarScrap.setNavigationOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            observeData()

            layoutEmpty.btnScrap.setOnClickListener {
                // TODO : 홈으로 가서 지도 띄워주기
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(Activity.RESULT_OK)
        return super.onKeyDown(keyCode, event)
    }

    private fun observeData() {
        viewModel.scrapUiState.flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    is ScrapViewModel.ScrapUiState.Success -> {
                        myScrapAdapter.submitList(it.data)
                        binding.isEmpty = false
                    }
                    is ScrapViewModel.ScrapUiState.Empty -> {
                        binding.isEmpty = true
                    }
                    is ScrapViewModel.ScrapUiState.Error -> {}
                    else -> {
                        // TODO : 로딩 에러 처리
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun initAdapter() {
        binding.rvScrap.adapter = myScrapAdapter
    }
}
