package org.helfoome.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.viewbinding.ViewBinding

object DialogUtil {
    fun makeDialog(context: Context, binding: ViewBinding, width: Int, height: Int): AlertDialog {

        return AlertDialog.Builder(context)
            .setView(binding.root)
            .show().apply {
                window?.setLayout(
                    width,
                    height
                )
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setCancelable(false)
            }
    }
}
