package org.helfoome.presentation.drawer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivitySettingBinding
import org.helfoome.presentation.MainViewModel
import org.helfoome.presentation.withdrawal.WithdrawalActivity
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        initListener()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        setResult(Activity.RESULT_OK)
        return super.onKeyDown(keyCode, event)
    }

    private fun initListener() {

        with(binding) {
            tvWithdrawal.setOnClickListener {
                startActivity(Intent(this@SettingActivity, WithdrawalActivity::class.java))
            }
            tvInquiry.setOnClickListener {
                sendGmail(R.string.setting_inquiry, R.string.mail_content_inquiry)
            }
            tvDeclaration.setOnClickListener {
                sendGmail(R.string.setting_declaration, R.string.mail_content_declaration)
            }
            ivBack.setOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            tvPrivacyPolicy.setOnClickListener {
                goToPrivacyPolicyPage()
            }
        }
    }

    private fun sendGmail(titleResId: Int, contentResId: Int) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts(
                "mailto", getString(R.string.healfoome_mail), null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(titleResId))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(contentResId))
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun goToPrivacyPolicyPage() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://chipped-hamburger-edb.notion.site/v-1-0-12ab557bcb7e45fe9308ad17177828d0")
                )
            )
            return
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}
