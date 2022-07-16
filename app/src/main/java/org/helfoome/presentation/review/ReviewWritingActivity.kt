package org.helfoome.presentation.review

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityReviewWritingBinding
import org.helfoome.presentation.type.HashtagViewType
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class ReviewWritingActivity : BindingActivity<ActivityReviewWritingBinding>(R.layout.activity_review_writing) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
       
    }
}
