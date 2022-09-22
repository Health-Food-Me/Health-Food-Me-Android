package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.helfoome.databinding.ItemEatingOutTipBinding
import org.helfoome.presentation.type.EatingOutTipType

class EatingOutTipView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var binding: ItemEatingOutTipBinding? = null

    init {
        orientation = VERTICAL
    }

    fun setTips(tips: List<String>, tipType: EatingOutTipType) {
        this.removeAllViews()
        for (i in tips.indices) {
            binding = ItemEatingOutTipBinding.inflate(LayoutInflater.from(context), this, false).apply {
                ivCheck.setImageResource(tipType.imgRes)
                tvAdvise.text = tips[i]
            }

            // FIXME 마지막 아이템 제외, bottom 패딩 적용 안됨
//            if (i < adviseList.size) {
//                binding.apply { setPadding(0, 0, 0, 16) } // TODO 16dp로 변경하기
//            }
            addView(binding?.root)
        }
    }
}
