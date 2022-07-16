package org.helfoome.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import org.helfoome.R
import org.helfoome.databinding.ActivityWithdrawalBinding
import org.helfoome.databinding.WithdrawDialogBinding
import org.helfoome.util.DialogUtil
import org.helfoome.util.binding.BindingActivity

class WithdrawalActivity : BindingActivity<ActivityWithdrawalBinding>(R.layout.activity_withdrawal) {
    private val withdrawalViewModel: WithdrawalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = withdrawalViewModel

        binding.btConfirm.setOnClickListener {
            val bind: WithdrawDialogBinding = WithdrawDialogBinding.inflate(LayoutInflater.from(this@WithdrawalActivity))
            val dialog = DialogUtil.makeDialog(this, bind, 288, 222)

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
