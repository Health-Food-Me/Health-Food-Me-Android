package org.helfoome.presentation.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.DialogMyReviewDeleteBinding

@AndroidEntryPoint
class ReviewDeleteFragmentDialog(private val reviewId: String) : DialogFragment() {

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    private var _binding: DialogMyReviewDeleteBinding? = null
    private val binding: DialogMyReviewDeleteBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_background)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogMyReviewDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.btnYes.setOnClickListener {
            myReviewViewModel.deleteReview(reviewId)
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
