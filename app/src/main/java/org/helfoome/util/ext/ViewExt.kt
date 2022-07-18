package org.helfoome.util.ext

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import org.helfoome.R
import timber.log.Timber

fun TextView.setAutoKeyword(restaurant: String, keyword: String?) {
    keyword?.let {
        text = SpannableStringBuilder(restaurant).apply {
            val index = restaurant.indexOf(keyword)
            Timber.d(keyword)
            if(index != -1) {
                setSpan(
                    ForegroundColorSpan(getColor(R.color.red_500)),
                    index,
                    index + keyword.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}
