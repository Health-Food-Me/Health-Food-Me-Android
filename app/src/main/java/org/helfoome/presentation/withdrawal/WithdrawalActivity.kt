package org.helfoome.presentation.withdrawal

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.databinding.ActivityWithdrawalBinding
import org.helfoome.presentation.splash.SplashActivity
import org.helfoome.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalActivity : BindingActivity<ActivityWithdrawalBinding>(R.layout.activity_withdrawal) {

    @Inject
    lateinit var storage: HFMSharedPreference
    private val withdrawalViewModel: WithdrawalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = withdrawalViewModel

        initObserves()
        initListener()
    }

    private fun initObserves() {
        withdrawalViewModel.nickname.observe(this) { nickname ->
            withdrawalViewModel.compareNickname(nickname)
        }
        withdrawalViewModel.withdrawSuccess.observe(this) {
            storage.clear()
            startActivity(Intent(this@WithdrawalActivity, SplashActivity::class.java))
            finishAffinity()
        }
    }

    private fun initListener() {
        with(binding) {
            ivBack.setOnClickListener {
                finish()
            }
            btConfirm.setOnClickListener {
                WithdrawFragmentDialog().show(supportFragmentManager, "WithdrawalDialog")
            }
        }
    }
}
