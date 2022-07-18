package org.helfoome.presentation.drawer

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import org.helfoome.R
import org.helfoome.databinding.ActivityProfileModifyBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.util.binding.BindingActivity

class ProfileModifyActivity : BindingActivity<ActivityProfileModifyBinding>(R.layout.activity_profile_modify) {
    private val viewModel: ProfileModifyViewModel by viewModels()
    private var animation: Animation? = null
    private var animator: Animation.AnimationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
        animator = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                val bottomTopAnimation = AnimationUtils.loadAnimation(this@ProfileModifyActivity, R.anim.anim_snackbar_bottom_top)
                binding.snvProfileModify.animation = bottomTopAnimation
                binding.snvProfileModify.setText("닉네임 설정 기준에 적합하지 않습니다")
            }
            override fun onAnimationRepeat(animation: Animation?) = Unit
        }
        initAnimation()
        initListener()
    }

    private fun initAnimation() {
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btModify.setOnClickListener {
            viewModel.checkNicknameFormat()
            // 중복 닉네임 체크 먼저 하기
            if (viewModel.isOverlapNickName.value == true) {
//                binding.snvProfileModify.animation = topDownAnimation
//                binding.snvProfileModify.setText("중복된 닉네임 입니다")
//                topDownAnimation.setAnimationListener(object : Animation.AnimationListener {
//
//                    override fun onAnimationStart(animation: Animation?) {
//                    }
//
//                    override fun onAnimationEnd(animation: Animation?) {
//                        binding.snvProfileModify.animation = bottomTopAnimation
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation?) {
//                    }
//                })
            } else if (viewModel.isValidNickname.value == true) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_snackbar_top_down)
                binding.snvProfileModify.animation = animation
                binding.snvProfileModify.setText("닉네임 설정 기준에 적합하지 않습니다")
                animator?.let { animation?.setAnimationListener(it) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animation?.cancel()
        animation = null
    }
}
