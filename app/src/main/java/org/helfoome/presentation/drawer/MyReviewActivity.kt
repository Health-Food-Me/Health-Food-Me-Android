package org.helfoome.presentation.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import org.helfoome.R
import org.helfoome.databinding.ActivityMyReviewBinding
import org.helfoome.databinding.MyreviewDialogBinding
import org.helfoome.domain.entity.MyReviewInfo
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.DELETE
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.EDIT
import org.helfoome.presentation.drawer.adapter.MyReviewAdapter.Companion.ENLARGE
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.startActivity

class MyReviewActivity : BindingActivity<ActivityMyReviewBinding>(R.layout.activity_my_review) {

    private lateinit var alertDialog: AlertDialog

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
                val layoutInflater = LayoutInflater.from(this@MyReviewActivity)
                val bind: MyreviewDialogBinding = MyreviewDialogBinding.inflate(layoutInflater)
                alertDialog = AlertDialog.Builder(this@MyReviewActivity)
                    .setView(bind.root)
                    .show()

                alertDialog.window?.setLayout(
                    288,
                    223
                )
                alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                alertDialog.setCancelable(false)

                bind.btnYes.setOnClickListener {
                    alertDialog.dismiss()
                }
                bind.btnNo.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rcvReview.adapter = myReviewAdapter
        myReviewAdapter.submitList(
            listOf(
                MyReviewInfo(
                    1, "s", 1F, listOf("11", "11"), "dsd", listOf("de", "de")
                ),
                MyReviewInfo(
                    1, "s", 1F, listOf("11", "11"), "dsd", listOf("de", "de")
                ),
                MyReviewInfo(
                    1, "s", 1F, listOf("11", "11"), "dsd", listOf("de", "de")
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
}
