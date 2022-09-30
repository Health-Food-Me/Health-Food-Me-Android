package org.helfoome.presentation.restaurant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.helfoome.databinding.DialogMapSelectionBinding
import org.helfoome.domain.entity.LocationPointInfo
import org.helfoome.presentation.MainViewModel

@AndroidEntryPoint
class MapSelectionBottomDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogMapSelectionBinding? = null
    private val binding: DialogMapSelectionBinding get() = requireNotNull(_binding)
    private val viewModel: MainViewModel by activityViewModels()
    private var startPoint: LocationPointInfo? = null
    private var endPoint: LocationPointInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogMapSelectionBinding.inflate(inflater, container, false)
        startPoint = viewModel.currentPoint.value
        endPoint = viewModel.selectedRestaurantPoint.value

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addListeners()
    }

    private fun addListeners() {
        binding.tvKakao.setOnClickListener {
            if (endPoint == null) return@setOnClickListener
            val kakaoMapScheme = if (startPoint == null) {
                "kakaomap://route?ep=${endPoint!!.lat},${endPoint!!.lng}&by=PUBLICTRANSIT"
            } else {
                "kakaomap://route?sp=${startPoint!!.lat},${startPoint!!.lng}&ep=${endPoint!!.lat},${endPoint!!.lng}&by=PUBLICTRANSIT"
            }

            moveToUrl(kakaoMapScheme, "market://details?id=net.daum.android.map")
        }
        binding.tvNaver.setOnClickListener {
            if (endPoint == null) return@setOnClickListener
            val naverMapScheme = if (startPoint == null) {
                "nmap://route/public?dlat=${endPoint!!.lat}&dlng=${endPoint!!.lng}&appname=org.sopt.healfoomedemo"
            } else {
                "nmap://route/public?slat=${startPoint!!.lat}&slng=${startPoint!!.lng}&dlat=${endPoint!!.lat}&dlng=${endPoint!!.lng}&appname=org.sopt.healfoomedemo"
            }

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
}
