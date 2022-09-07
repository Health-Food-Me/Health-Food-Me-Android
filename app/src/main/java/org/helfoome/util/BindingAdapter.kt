package org.helfoome.util

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import org.helfoome.util.ext.getDp
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

@BindingAdapter("android:layout_marginTop")
fun View.layoutMarginTop(margin: Int) {
    if (margin != 0) {
        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.topMargin = margin.getDp
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
