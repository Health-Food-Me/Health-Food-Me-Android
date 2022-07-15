package org.helfoome.presentation

import android.content.Intent
import android.os.Bundle
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.ActivityLoginBinding
import org.helfoome.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initListeners()
        initObserve()
    }

    private fun initListeners() {
        binding.ivNaverLogin.setOnClickListener {
            naverLogin()
        }
        binding.ivKakaoLogin.setOnClickListener {
            kakaoLogin()
        }
    }

    private fun initObserve() {
        naverAuthService.loginSuccess.observe(this) {
            if (naverAuthService.loginSuccess.value == true) {
                goToMain()
            }
        }
        kakaoAuthService.loginSuccess.observe(this) {
            if (kakaoAuthService.loginSuccess.value == true) {
                goToMain()
            }
        }
    }

    private fun naverLogin() {
        NaverIdLoginSDK.authenticate(this, naverAuthService)
    }

    private fun kakaoLogin() {
        kakaoAuthService.kakaoLogin()
    }

    private fun goToMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
