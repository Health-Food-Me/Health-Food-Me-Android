package org.helfoome.presentation.drawer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import com.naver.maps.map.app.LegalNoticeActivity
import com.naver.maps.map.app.OpenSourceLicenseActivity
import org.helfoome.R
import org.helfoome.databinding.ActivitySettingBinding
import org.helfoome.presentation.WithdrawalActivity
import org.helfoome.util.binding.BindingActivity

class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initListener()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(Activity.RESULT_OK)
        return super.onKeyDown(keyCode, event)
    }

    private fun initListener() {

        binding.tvWithdrawal.setOnClickListener {
            startActivity(Intent(this, WithdrawalActivity::class.java))
        }
        binding.tvNaverMapNotice.setOnClickListener {
            startActivity(Intent(this, LegalNoticeActivity::class.java))
        }
        binding.tvNaverMapOpenSource.setOnClickListener {
            startActivity(Intent(this, OpenSourceLicenseActivity::class.java))
        }
        binding.ivBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
