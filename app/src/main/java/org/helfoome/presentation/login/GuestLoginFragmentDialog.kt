package org.helfoome.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.DialogGuestLoginSupportBinding
import org.helfoome.presentation.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class GuestLoginFragmentDialog : DialogFragment() {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService

    private var _binding: DialogGuestLoginSupportBinding? = null
    private val binding: DialogGuestLoginSupportBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_background)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogGuestLoginSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun initListener() {
        binding.ivCancel.setOnClickListener {
            dismiss()
        }
        binding.ivKakaoLogin.setOnClickListener {
            kakaoAuthService.kakaoLogin(
                ::startMain
            )
        }
        binding.ivNaverLogin.setOnClickListener {
            NaverIdLoginSDK.authenticate(
                requireContext(),
                naverAuthService.apply {
                    loginListener = ::startMain
                }
            )
        }
    }

    private fun startMain() {
        startActivity(
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        )
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
