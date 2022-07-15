package org.helfoome.presentation.drawer

import android.content.Intent
import android.os.Bundle
import org.helfoome.R
import org.helfoome.databinding.ActivitySettingBinding
import org.helfoome.presentation.WithdrawalActivity
import org.helfoome.util.binding.BindingActivity

class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvWithdrawal.setOnClickListener {
            startActivity(Intent(this, WithdrawalActivity::class.java))
        }
    }
}
