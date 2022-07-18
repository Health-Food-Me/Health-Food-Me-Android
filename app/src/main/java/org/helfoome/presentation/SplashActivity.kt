package org.helfoome.presentation

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.helfoome.R
import org.helfoome.databinding.ActivitySplashBinding
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.makeTransparentStatusBar

class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.makeTransparentStatusBar()

        lifecycleScope.launch(Dispatchers.Main) {
            job = launch {
                delay(2500)
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
