package org.helfoome.presentation.drawer

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityProfileModifyBinding
import org.helfoome.util.SnackBarTopDown
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class ProfileModifyActivity : BindingActivity<ActivityProfileModifyBinding>(R.layout.activity_profile_modify) {
    private val viewModel: ProfileModifyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initListener()
        initObserve()
    }

    private fun initObserve() {
        viewModel.isOverlapNickName.observe(this) {
            if (viewModel.isOverlapNickName.value == false) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding, R.id.snv_profile_modify, "중복된 닉네임 입니다")
            }
        }

        viewModel.isValidNickname.observe(this) {
            if (viewModel.isValidNickname.value == false) {
                SnackBarTopDown.makeSnackBarTopDown(this, binding, R.id.snv_profile_modify, "닉네임 설정 기준에 적합하지 않습니다")
            }
        }

        viewModel.isProfileModify.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btModify.setOnClickListener {
            viewModel.profileModify()
        }
    }
}
