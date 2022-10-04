package org.helfoome.presentation.alert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.DialogAlertBinding
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.presentation.review.RestaurantReviewWritingViewModel
import org.helfoome.presentation.type.AlertType

@AndroidEntryPoint
class AlertFragmentDialog(private val alertType: AlertType) : DialogFragment() {

    private val alertViewModel: AlertViewModel by viewModels()
    private val restaurantReviewWritingViewModel: RestaurantReviewWritingViewModel by activityViewModels()

    private var _binding: DialogAlertBinding? = null
    private val binding: DialogAlertBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_background)
        isCancelable = false
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogAlertBinding.inflate(inflater, container, false)
        binding.alertViewModel = alertViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        alertViewModel.setAlertType(alertType)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        binding.btnYes.setOnClickListener {
            when (alertType) {
                AlertType.LOGOUT -> {
                    alertViewModel.logout()
                    startMain()
                }
                AlertType.WRITE_CANCEL -> restaurantReviewWritingViewModel.setIsYesClicked(true)
                AlertType.EDIT_CANCEL -> restaurantReviewWritingViewModel.setIsYesClicked(true)
                AlertType.DELETE_REVIEW -> {}
            }
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun startMain() {
        startActivity(
            Intent(context, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
