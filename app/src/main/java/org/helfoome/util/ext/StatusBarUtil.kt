package org.helfoome.util.ext

import android.view.Window
import android.view.WindowManager

fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}
