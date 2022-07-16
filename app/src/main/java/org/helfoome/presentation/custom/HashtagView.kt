package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import org.helfoome.databinding.ItemHashtagRestaurantSummaryBinding
import org.helfoome.databinding.ItemHashtagReviewTabBinding
import org.helfoome.presentation.type.HashtagViewType

class HashtagView(context: Context, attrs: AttributeSet? = null) : FlexboxLayout(context, attrs) {
    private lateinit var inflater: LayoutInflater

    init {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(context)
        flexWrap = FlexWrap.WRAP
    }

    fun setHashtag(hashtag: List<String>, viewType: HashtagViewType) {
        for (i in hashtag.indices) {
            val binding = when (viewType) {
                HashtagViewType.REVIEW_TAB_TYPE -> ItemHashtagReviewTabBinding.inflate(inflater, this, false).apply {
                    tvHashtag.text = String.format(context.getString(viewType.strRes ?: return), hashtag[i])
                }
                HashtagViewType.RESTAURANT_SUMMARY_TYPE -> ItemHashtagRestaurantSummaryBinding.inflate(inflater, this, false).apply {
                    tvHashtag.text = hashtag[i]
                }
            }

            addView(binding.root)
        }
    }
}
