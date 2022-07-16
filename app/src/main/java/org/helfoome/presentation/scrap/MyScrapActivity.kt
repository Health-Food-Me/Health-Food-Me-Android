package org.helfoome.presentation.scrap

import android.os.Bundle
import org.helfoome.R
import org.helfoome.databinding.ActivityMyScrapBinding
import org.helfoome.presentation.scrap.adapter.MyScrapAdapter
import org.helfoome.util.binding.BindingActivity

class MyScrapActivity : BindingActivity<ActivityMyScrapBinding>(R.layout.activity_my_scrap) {
    private val myScrapAdapter = MyScrapAdapter()

    // TODO : 서버 통신 연동 필요
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        binding.toolbarScrap.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        binding.rvScrap.adapter = myScrapAdapter
    }
}
