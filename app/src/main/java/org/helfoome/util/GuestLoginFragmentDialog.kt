package org.helfoome.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.DialogGuestLoginSupportBinding
import org.helfoome.presentation.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class GuestLoginFragmentDialog(context: Context): DialogFragment() {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService

    @Inject
    lateinit var storage: HFMSharedPreference

    private lateinit var binding: DialogGuestLoginSupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_background)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogGuestLoginSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.ivCancel.setOnClickListener {
            dismiss()
        }
        binding.ivKakaoLogin.setOnClickListener {
            storage.isGuestLogin = false
            kakaoAuthService.kakaoLogin(
                ::startMain
            )
        }
        binding.ivNaverLogin.setOnClickListener {
            storage.isGuestLogin = false
            NaverIdLoginSDK.authenticate(
                requireContext(),
                naverAuthService.apply {
                    loginListener = ::startMain
                }
            )
        }
    }

    private fun startMain() {
        startActivity(Intent(context, MainActivity::class.java))
    }
}