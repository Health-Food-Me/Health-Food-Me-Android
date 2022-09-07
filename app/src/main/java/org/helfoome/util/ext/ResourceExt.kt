package org.helfoome.util.ext

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun View.getColor(@ColorRes res: Int) = context.getColor(res)
fun Fragment.getColor(@ColorRes res: Int) = requireContext().getColor(res)
fun View.getString(@StringRes res: Int) = context.getString(res)
fun Fragment.getString(@StringRes res: Int) = requireContext().getString(res)
fun View.getDimen(@DimenRes res: Int) = context.resources.getDimension(res).toInt()
fun Context.getColor(@ColorRes res: Int) = ContextCompat.getColor(this, res)
val Int.getDp: Int
    get() {
        val metrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
            .toInt()
    }