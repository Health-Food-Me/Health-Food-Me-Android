package org.helfoome.presentation.alert

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.helfoome.R
import org.helfoome.databinding.DialogConfirmBinding
import org.helfoome.presentation.type.ConfirmType

class ConfirmFragmentDialog : DialogFragment() {
    private var _binding: DialogConfirmBinding? = null
    private val binding: DialogConfirmBinding get() = requireNotNull(_binding)

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
        _binding = DialogConfirmBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirmType = confirmType
        initListener()
    }

    private fun initListener() {
        binding.btnConfirm.setOnClickListener {
            when (confirmType) {
                ConfirmType.NETWORK_CONFIRM -> dismiss()
                ConfirmType.LOGIN_CONFIRM -> dismiss()
                ConfirmType.LOCATION_CONFIRM -> {
                    dismiss()
                    startSetting()
                }
            }
        }
    }

    private fun startSetting() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity?.packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ConfirmFragmentDialog"
        private lateinit var confirmType: ConfirmType

        fun newInstance(confirmType: ConfirmType): ConfirmFragmentDialog {
            this.confirmType = confirmType
            return ConfirmFragmentDialog()
        }
    }
}
