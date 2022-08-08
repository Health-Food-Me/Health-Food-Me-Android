package org.helfoome.presentation.splash

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import org.helfoome.R
import org.helfoome.databinding.ActivitySplashSecondBinding
import org.helfoome.presentation.login.LoginActivity
import org.helfoome.util.binding.BindingActivity
import org.helfoome.util.ext.startActivity

class SplashSecondActivity : BindingActivity<ActivitySplashSecondBinding>(R.layout.activity_splash_second) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_splash_transparency_one_to_zero)
        binding.tvLogo.animation = splashAnimation
        binding.ivDescription.animation = splashAnimation

        splashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                startActivity<LoginActivity>()
                overridePendingTransition(0, 0)
                finish()
            }
        })
    }
}
