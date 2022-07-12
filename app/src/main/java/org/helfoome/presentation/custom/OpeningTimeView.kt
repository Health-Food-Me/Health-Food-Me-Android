package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.helfoome.databinding.ViewOpeningTimeBinding

class OpeningTimeView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var binding: ViewOpeningTimeBinding
    private lateinit var inflater: LayoutInflater

    init {
        initView()
        initListeners()
    }

    private fun initView() {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(context)
        binding = ViewOpeningTimeBinding.inflate(inflater, this, true)
    }

    private fun initListeners() {
        binding.btnDropdown.isSelected = false
        binding.layoutNextday.visibility = GONE
        binding.btnDropdown.setOnClickListener {
            with(binding) {
                btnDropdown.isSelected = !btnDropdown.isSelected
                layoutNextday.visibility = if (btnDropdown.isSelected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    fun setText(timeList: List<String>) {
        // TODO 서버 응답값 확인 후 로직 구현 예정
        binding.tvToday.text = timeList[0]
        binding.tvNextday1.text = timeList[1]
        binding.tvNextday2.text = timeList[2]
        binding.tvNextday3.text = timeList[3]
        binding.tvNextday4.text = timeList[4]
        binding.tvNextday5.text = timeList[5]
        binding.tvNextday6.text = timeList[6]
    }
}