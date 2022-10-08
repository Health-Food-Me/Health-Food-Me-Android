package org.helfoome.presentation.splash

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.databinding.ActivitySplashBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.makeTransparentStatusBar
import org.helfoome.util.ext.startActivity
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    // TODO : 필드 주입 수정
    @Inject
    lateinit var storage: HFMSharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.makeTransparentStatusBar()

        lifecycleScope.launch {
            delay(2500)
            if (storage.isLogin) startActivity<MainActivity>() else startActivity<SplashSecondActivity>()
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
