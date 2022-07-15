package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.helfoome.R
import org.helfoome.databinding.ItemHashtagBinding

class HashtagView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private var binding: ItemHashtagBinding? = null
    private lateinit var inflater: LayoutInflater

    init {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(context)
    }

    fun setHashtag(hashtag: List<String>, isContainSharp: Boolean = true) {
        for (i in hashtag.indices) {
            binding = ItemHashtagBinding.inflate(inflater, this, false).apply {
                tvHashtag.text = if (isContainSharp) String.format(context.getString(R.string.format_hashtag), hashtag[i]) else hashtag[i]
            }

            addView(binding?.root)
        }
    }
}
