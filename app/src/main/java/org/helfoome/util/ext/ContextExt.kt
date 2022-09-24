package org.helfoome.util.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.getScreenSize(resize: Int): Pair<Int, Int> {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = wm.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        Pair(
            windowMetrics.bounds.width() - insets.left - insets.right - resize,
            windowMetrics.bounds.height() - insets.bottom - insets.top - resize
        )
    } else {
        val metrics: DisplayMetrics = resources.displayMetrics
        val displayPixelWidth = metrics.widthPixels
        val displayPixelHeight = metrics.heightPixels
        Pair(displayPixelWidth - displayPixelWidth / 5, displayPixelHeight)
    }
}

fun Context.closeKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

inline fun <reified T : Activity> Context.buildIntent(
    vararg argument: Pair<String, Any?>
) = Intent(this, T::class.java).apply {
    putExtras(bundleOf(*argument))
}

inline fun <reified T : Activity> Context.startActivity(
    vararg argument: Pair<String, Any?>
) {
    startActivity(buildIntent<T>(*argument))
}

fun Context.stringListFrom(id: Int): List<String> =
    resources.getStringArray(id).toList()

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
