package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.helfoome.databinding.ItemEatingOutAdviseBinding
import org.helfoome.presentation.type.EatingOutAdviseType

class EatingOutAdviseView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var binding: ItemEatingOutAdviseBinding? = null

    init {
        orientation = VERTICAL
    }

    fun setAdviseList(adviseList: List<String>, adviseType: EatingOutAdviseType) {
        for (i in adviseList.indices) {
            binding = ItemEatingOutAdviseBinding.inflate(LayoutInflater.from(context), this, false).apply {
                ivCheck.setImageResource(adviseType.imgRes)
                tvAdvise.text = adviseList[i]
            }

            // FIXME 마지막 아이템 제외, bottom 패딩 적용 안됨
//            if (i < adviseList.size) {
//                binding.apply { setPadding(0, 0, 0, 16) } // TODO 16dp로 변경하기
//            }
            addView(binding?.root)
        }
    }
}
