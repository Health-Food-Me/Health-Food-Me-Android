package org.helfoome.presentation.restaurant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.DialogMapSelectionBinding
import org.helfoome.domain.entity.LocationPointInfo

@AndroidEntryPoint
class MapSelectionBottomDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogMapSelectionBinding? = null
    private val binding: DialogMapSelectionBinding get() = requireNotNull(_binding)
    private lateinit var startPoint: LocationPointInfo
    private lateinit var endPoint: LocationPointInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startPoint = it.getParcelable(ARG_START_POINT) ?: return
            endPoint = it.getParcelable(ARG_END_POINT) ?: return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogMapSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.tvKakao.setOnClickListener {
            val kakaoMapScheme =
                "kakaomap://route?sp=${startPoint.lat},${startPoint.lng}&ep=${endPoint.lat},${endPoint.lng}&by=PUBLICTRANSIT"
            moveToUrl(kakaoMapScheme, "market://details?id=net.daum.android.map")
        }
        binding.tvNaver.setOnClickListener {
            val naverMapScheme =
                "nmap://route/public?slat=${startPoint.lat}&slng=${startPoint.lng}&dlat=${endPoint.lat}&dlng=${endPoint.lng}&appname=org.sopt.healfoomedemo"
            moveToUrl(naverMapScheme, "market://details?id=com.nhn.android.nmap")
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun moveToUrl(url: String, marketUrl: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val list = requireActivity().packageManager.queryIntentActivities(intent, 0)
        startActivity(if (list.isEmpty()) Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)) else intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_START_POINT = "startPoint"
        const val ARG_END_POINT = "endPoint"
    }
}
