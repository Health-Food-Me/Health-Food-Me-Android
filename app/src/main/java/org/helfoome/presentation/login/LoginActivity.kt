package org.helfoome.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.service.KakaoAuthService
import org.helfoome.data.service.NaverAuthService
import org.helfoome.databinding.ActivityLoginBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.presentation.MainViewModel
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.startActivity
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var naverAuthService: NaverAuthService

    @Inject
    lateinit var kakaoAuthService: KakaoAuthService

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAnimation()
        initListeners()
    }

    private fun initAnimation() {
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_transparency_zero_to_one)
        binding.tvLogo.animation = splashAnimation
        binding.ivDescription.animation = splashAnimation
    }

    private fun initListeners() {
        binding.ivNaverLogin.setOnClickListener {
            viewModel.setIsGuestLogin(false)
            naverLogin()
        }
        binding.ivKakaoLogin.setOnClickListener {
            viewModel.setIsGuestLogin(false)
            kakaoLogin()
        }
        binding.tvGuest.setOnClickListener {
            viewModel.setIsGuestLogin(true)
            startActivity<MainActivity>()
        }
    }

    private fun startMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun naverLogin() {
        NaverIdLoginSDK.authenticate(
            this,
            naverAuthService.apply {
                loginListener = ::startMain
            }
        )
    }

    private fun kakaoLogin() {
        kakaoAuthService.kakaoLogin(
            ::startMain
        )
    }
}
