package org.helfoome.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.HFMApplication
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.ActivityLoginBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.login.viewmodel.LoginViewModel
import org.helfoome.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        initListeners()
    }

    private fun initListeners() {
        binding.ivNaverLogin.setOnClickListener {
            naverLogin()
        }
        binding.ivKakaoLogin.setOnClickListener {
            kakaoLogin()
        }
    }

    private fun startMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun naverLogin() {
        NaverIdLoginSDK.authenticate(this,
            naverAuthService.apply {
                loginListener = ::startMain
            }
        )
    }

    private fun kakaoLogin() {
        kakaoAuthService.kakaoLogin()
        viewModel.loginNetwork("kakao")
    }
}
