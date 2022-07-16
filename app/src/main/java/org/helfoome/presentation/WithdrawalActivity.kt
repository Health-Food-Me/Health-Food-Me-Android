package org.helfoome.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityWithdrawalBinding
import org.helfoome.databinding.DialogWithdrawBinding
import org.helfoome.util.DialogUtil
import org.helfoome.util.ResolutionMetrics
import org.helfoome.util.binding.BindingActivity
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawalActivity : BindingActivity<ActivityWithdrawalBinding>(R.layout.activity_withdrawal) {
    @Inject
    lateinit var resolutionMetrics: ResolutionMetrics
    private val withdrawalViewModel: WithdrawalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = withdrawalViewModel

        initListener()
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btConfirm.setOnClickListener {
            val bind = DialogWithdrawBinding.inflate(LayoutInflater.from(this@WithdrawalActivity))
            val dialog = DialogUtil.makeDialog(this, bind, resolutionMetrics.toPixel(288), resolutionMetrics.toPixel(222))

            bind.btnYes.setOnClickListener {
                startActivity(Intent(this@WithdrawalActivity, LoginActivity::class.java))
                finish()
            }
            bind.btnNo.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}
