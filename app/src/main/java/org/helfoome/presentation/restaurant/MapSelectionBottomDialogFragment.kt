package org.helfoome.presentation.restaurant

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.DialogMapSelectionBinding

@AndroidEntryPoint
class MapSelectionBottomDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogMapSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogMapSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.tvKakao.setOnClickListener {
            val kakaoMapScheme = "kakaomap://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=PUBLICTRANSIT"
            moveToUrl(kakaoMapScheme, "market://details?id=net.daum.android.map")
        }
        binding.tvNaver.setOnClickListener {
            val naverMapScheme =
                "nmap://route/public?slat=37.4640070&slng=126.9522394&sname=%EC%84%9C%EC%9A%B8%EB%8C%80%ED%95%99%EA%B5%90&dlat=37.5209436&dlng=127.1230074&dname=%EC%98%AC%EB%A6%BC%ED%94%BD%EA%B3%B5%EC%9B%90&appname=org.sopt.healfoomedemo"
            moveToUrl(naverMapScheme, "market://details?id=com.nhn.android.nmap")
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun moveToUrl(url: String, marketUrl: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val list =
            activity?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: return
        if (list.isEmpty()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)))
        } else {
            startActivity(intent)
        }
    }
}
