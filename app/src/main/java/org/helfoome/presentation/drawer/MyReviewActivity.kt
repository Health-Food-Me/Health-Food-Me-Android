package org.helfoome.presentation.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import org.helfoome.R
import org.helfoome.databinding.ActivityMyReviewBinding
import org.helfoome.databinding.MyreviewDialogBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.DELETE
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.EDIT
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.ENLARGE
import org.helfoome.util.DialogUtil
import org.helfoome.util.binding.BindingActivity
import android.view.LayoutInflater
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.MyreviewDialogBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.DELETE
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.EDIT
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.ENLARGE
import org.helfoome.util.DialogUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.ext.startActivity
import javax.inject.Inject
import org.helfoome.util.ext.startActivity

@AndroidEntryPoint
class MyReviewActivity : BindingActivity<ActivityMyReviewBinding>(R.layout.activity_my_review) {

    private val myReviewAdapter = MyReviewAdapter {
        adapterClickListener(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Inject
        lateinit var resolutionMetrics: ResolutionMetrics

        private val myReviewAdapter = MyReviewAdapter {
            adapterClickListener(it)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding.rcvReview.visibility = View.VISIBLE
            binding.layoutEmptyView.visibility = View.GONE
            initAdapter()
            initListeners()
//        if (dataset.isEmpty()) {
//            binding.rcvReview.visibility = View.GONE
//            binding.layoutEmptyView.visibility = View.VISIBLE
//        }
//        else {
//        binding.rcvReview.visibility = View.VISIBLE
//        binding.layoutEmptyView.visibility = View.GONE
//        }
        }

        private fun adapterClickListener(it: Int) {
            when (it) {
                EDIT -> {
                    startActivity<MainActivity>()
                }
                ENLARGE -> {
                    startActivity<MainActivity>()
                }
                DELETE -> {
                    val bind = MyreviewDialogBinding.inflate(LayoutInflater.from(this@MyReviewActivity))
                    val dialog = DialogUtil.makeDialog(this, bind, resolutionMetrics.toPixel(288), resolutionMetrics.toPixel(223))

                    bind.btnYes.setOnClickListener {
                        dialog.dismiss()
                    }
                    bind.btnNo.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }
        }

        private fun initAdapter() {
            binding.rcvReview.adapter = myReviewAdapter
            myReviewAdapter.submitList(
                listOf(
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    ),
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    ),
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    )
                )
            )
        }

        private fun initListeners() {
            binding.btnGoToStore.setOnClickListener {
                finish()
            }
            binding.ivBack.setOnClickListener {
                finish()
            }
        }

        private fun adapterClickListener(it: Int) {
            when (it) {
                EDIT -> {
                    startActivity<MainActivity>()
                }
                ENLARGE -> {
                    startActivity<MainActivity>()
                }
                DELETE -> {
                    val bind = MyreviewDialogBinding.inflate(LayoutInflater.from(this@MyReviewActivity))
                    val dialog = DialogUtil.makeDialog(this, bind, 288, 223)

                    bind.btnYes.setOnClickListener {
                        dialog.dismiss()
                    }
                    bind.btnNo.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }
        }

        private fun initAdapter() {
            binding.rcvReview.adapter = myReviewAdapter
            myReviewAdapter.submitList(
                listOf(
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    ),
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    ),
                    MyReviewInfo(
                        1, "s", 1f, listOf("11", "11"), "dsd", listOf("de", "de")
                    )
                )
            )
        }
    }
}
