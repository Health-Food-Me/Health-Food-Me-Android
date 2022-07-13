package org.helfoome.util

import android.util.DisplayMetrics
import androidx.annotation.Px
import javax.inject.Inject
import kotlin.math.roundToInt

class ResolutionMetrics @Inject constructor(private val displayMetrics: DisplayMetrics) {
    val screenWidth: Int
        get() = displayMetrics.widthPixels

    val screenHeight: Int
        get() = displayMetrics.heightPixels

    @Px
    fun toPixel(dp: Int) = (dp * displayMetrics.density).roundToInt()

    fun toDP(@Px pixel: Int) = (pixel / displayMetrics.density).roundToInt()
}
