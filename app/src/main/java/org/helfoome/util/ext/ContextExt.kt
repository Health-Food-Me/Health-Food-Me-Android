package org.helfoome.util.ext

import android.content.Context
import android.widget.Toast

fun Context.stringListFrom(id: Int): List<String> =
    resources.getStringArray(id).toList()

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
