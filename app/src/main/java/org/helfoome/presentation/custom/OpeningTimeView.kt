package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.helfoome.R
import org.helfoome.databinding.ViewOpeningTimeBinding
import org.helfoome.util.DateUtil

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
        binding = ViewOpeningTimeBinding.inflate(inflater, this, true).apply {
            btnDropdown.isSelected = false
            layoutNextday.visibility = GONE
        }
    }

    private fun initListeners() {
        binding.btnDropdown.setOnClickListener {
            controlVisibility()
        }
        binding.layoutNextday.setOnClickListener {
            controlVisibility()
        }
        binding.tvToday.setOnClickListener {
            controlVisibility()
        }
    }

    private fun controlVisibility() {
        with(binding) {
            btnDropdown.isSelected = !btnDropdown.isSelected
            tvToday.typeface = resources.getFont(if (btnDropdown.isSelected) R.font.notosanskr_b else R.font.notosanskr_m)
            layoutNextday.visibility = if (btnDropdown.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    fun setText(timeList: List<String>?) {
        if (timeList == null || timeList.isEmpty()) return
        val tvDayOfWeek = with(binding) { listOf(tvToday, tvNextday1, tvNextday2, tvNextday3, tvNextday4, tvNextday5, tvNextday6) }
        val today = DateUtil.getTodayDayOfWeek()

        for (i in timeList.indices) {
            val day = (today.index + i) % timeList.size
            tvDayOfWeek[i].text = "${context.getString(DateUtil.convertIndexToDayOfWeek(day).strRes)} ${timeList[day]}"
        }
    }
}
