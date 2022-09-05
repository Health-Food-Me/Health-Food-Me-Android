package org.helfoome.presentation.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.R
import org.helfoome.databinding.ActivityWebViewBinding
import org.helfoome.util.binding.BindingActivity

@AndroidEntryPoint
class WebViewActivity : BindingActivity<ActivityWebViewBinding>(R.layout.activity_web_view) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding.webView.apply {
            // 새 창 띄우지 않기
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient() // 크롬환경에 맞는 세팅을 해줌

            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportZoom(true)
                loadWithOverviewMode = true
                useWideViewPort = true
            }

            intent.getStringExtra(ARG_WEB_VIEW_LINK)?.let { loadUrl(it) }
        }
    }

    companion object {
        private const val ARG_WEB_VIEW_LINK = "link"
    }
}
