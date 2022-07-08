package org.helfoome.util

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("app:imageUrl")
fun ImageView.setImage(imageUrl: String?) {
    if (imageUrl == null) return
    load(imageUrl)
}

@BindingAdapter("app:visibility")
fun View.setVisibility(isVisible: Boolean) {
    this.isVisible = isVisible
}
