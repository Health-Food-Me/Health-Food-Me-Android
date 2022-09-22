package org.helfoome.util

import android.content.res.Resources
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import org.helfoome.R
import org.helfoome.presentation.type.AlertType
import org.helfoome.presentation.custom.EatingOutTipView
import org.helfoome.presentation.type.EatingOutTipType
import java.text.DecimalFormat

@BindingAdapter("app:imageUrl")
fun ImageView.setImage(imageUrl: String?) {
    if (imageUrl == null || imageUrl == "") return
    load(imageUrl)
}

@BindingAdapter("app:visibility")
fun View.setVisibility(isVisible: Boolean?) {
    if (isVisible == null) return
    this.isVisible = isVisible
}

@BindingAdapter("app:layout_marginTop")
fun View.layoutMarginTop(margin: Int) {
    if (margin != 0) {
        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        val metrics = Resources.getSystem().displayMetrics
        layoutParams.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin.toFloat(), metrics)
            .toInt()
        this.layoutParams = layoutParams
    }
}

@BindingAdapter("app:isSelected")
fun View.setSelected(isSelected: Boolean?) {
    if (isSelected == null) return
    this.isSelected = isSelected
}

@BindingAdapter("app:distance")
fun TextView.setDistance(distance: Int?) {
    if (distance == null) return
    this.text = if (distance >= 1000) {
        val df = DecimalFormat("#.#")
        val result = distance / 1000.0
        "${df.format(result)}km"
    } else {
        "${distance}m"
    }
}

@BindingAdapter("app:eatingTips")
fun EatingOutTipView.setEatingTips(eatingTipList: List<String>?) {
    this.setTips(eatingTipList ?: return, EatingOutTipType.EATING_TIP)
}

@BindingAdapter("app:recommendationTips")
fun EatingOutTipView.setRecommendationTips(recommendationTipList: List<String>?) {
    this.setTips(recommendationTipList ?: return, EatingOutTipType.RECOMMENDATION_TIP)
}

@BindingAdapter("app:textAppearance")
fun TextView.setTextStyle(styleResId: Int) {
    this.setTextAppearance(styleResId)
}
