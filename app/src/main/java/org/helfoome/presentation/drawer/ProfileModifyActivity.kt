package org.helfoome.presentation.drawer

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_FADE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import org.helfoome.R
import org.helfoome.databinding.ActivityProfileModifyBinding
import org.helfoome.presentation.MainActivity
import org.helfoome.util.binding.BindingActivity

class ProfileModifyActivity : BindingActivity<ActivityProfileModifyBinding>(R.layout.activity_profile_modify) {
    private val viewModel: ProfileModifyViewModel by viewModels()
    private lateinit var snackBarView: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initSnackBar()
        initObservers()
        initListener()
    }

    private fun initListener() {
        binding.btModify.setOnClickListener {
            viewModel.checkNicknameFormat()
            if (viewModel.isValidNickname.value == true) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                snackBarView.show()
            }
        }

        binding.etNickname.setOnTouchListener { v, event ->
            snackBarView.dismiss()
            false
        }
    }

    private fun initSnackBar() {
        snackBarView = Snackbar.make(binding.layoutProfileModify, "닉네임 설정 기준에 적합하지 않습니다", LENGTH_LONG)
        val view = snackBarView.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(30, 26, 30, 0)
        view.layoutParams = params
        view.background = ContextCompat.getDrawable(this, R.drawable.rectangle_grey_radius_77) // for custom background
        snackBarView.animationMode = ANIMATION_MODE_FADE
    }

    private fun initObservers() {
        viewModel.isValidNickname.observe(this) {
            if (it == false) {
                snackBarView.show()
            } else {
                snackBarView.dismiss()
            }
        }
    }
}
