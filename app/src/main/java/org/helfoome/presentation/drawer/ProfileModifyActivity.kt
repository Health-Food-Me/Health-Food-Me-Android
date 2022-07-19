package org.helfoome.presentation.drawer

import android.app.Activity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.databinding.ActivityProfileModifyBinding
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
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
                if (viewModel.isOverlapNickName.value == false) {
                    binding.snvProfileModify.setText("중복된 닉네임 입니다")
                } else if (viewModel.isValidNickname.value == false) {
                    binding.snvProfileModify.setText("닉네임 설정 기준에 적합하지 않습니다")
                }
            }

            override fun onAnimationRepeat(animation: Animation?) = Unit
        }
        initListener()
        initObserve()
    }

    private fun initObserve() {
        viewModel.isOverlapNickName.observe(this) {
            if (viewModel.isOverlapNickName.value == false) {
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_snackbar_top_down)
                binding.snvProfileModify.animation = animation
                binding.snvProfileModify.setText("중복된 닉네임 입니다")
                animator?.let {
                    animation?.setAnimationListener(it)
                }
            }
        }

        viewModel.isValidNickname.observe(this) {
            if (viewModel.isValidNickname.value == false) {
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_snackbar_top_down)
                binding.snvProfileModify.animation = animation
                binding.snvProfileModify.setText("닉네임 설정 기준에 적합하지 않습니다")
                animator?.let { animation?.setAnimationListener(it) }
            }
        }

        viewModel.isProfileModify.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btModify.setOnClickListener {
            viewModel.profileModify()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animation?.cancel()
        animation = null
    }
}
