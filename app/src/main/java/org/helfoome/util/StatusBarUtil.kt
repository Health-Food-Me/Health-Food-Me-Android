package org.helfoome.util

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}
