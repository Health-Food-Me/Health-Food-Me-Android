package org.helfoome.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.DialogAlertBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.presentation.type.AlertType
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlertFragmentDialog(val alertType: AlertType, val width: Int, val height: Int) : DialogFragment() {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: DialogAlertBinding? = null
    private val binding: DialogAlertBinding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_background)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogAlertBinding.inflate(inflater, container, false)
        binding.alertType = alertType
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            width,
            height
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btnYes.setOnClickListener {
            when(alertType) {
                AlertType.LOGOUT -> logout()
                else -> {}
            }
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }

    private fun logout() {
        NaverIdLoginSDK.logout()
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
            } else {
                Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
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
