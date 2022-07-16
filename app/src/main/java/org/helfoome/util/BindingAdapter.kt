package org.helfoome.util

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load
import org.helfoome.R

@BindingAdapter("app:imageUrl")
fun ImageView.setImage(imageUrl: String?) {
    imageUrl?.let {
        if (imageUrl == "")
            load(R.drawable.ic_empty)
        else
            load(imageUrl)
    }
}

@BindingAdapter("app:visibility")
fun View.setVisibility(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("app:isSelected")
fun View.setSelected(isSelected: Boolean) {
    this.isSelected = isSelected
}
