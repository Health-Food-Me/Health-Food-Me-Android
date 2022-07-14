package org.helfoome.presentation

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import org.helfoome.R
import org.helfoome.databinding.ActivityWithdrawalBinding
import org.helfoome.databinding.WithdrawDialogBinding
import org.helfoome.util.binding.BindingActivity

class WithdrawalActivity : BindingActivity<ActivityWithdrawalBinding>(R.layout.activity_withdrawal) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btConfirm.setOnClickListener {
            val layoutInflater = LayoutInflater.from(this@WithdrawalActivity)
            val bind: WithdrawDialogBinding = WithdrawDialogBinding.inflate(layoutInflater)
            val alertDialog = AlertDialog.Builder(this@WithdrawalActivity)
                .setView(bind.root)
                .show()

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            bind.btnYes.setOnClickListener {
                startActivity(Intent(this@WithdrawalActivity, LoginActivity::class.java))
                finish()
            }
            bind.btnNo.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}