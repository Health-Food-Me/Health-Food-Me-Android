package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.helfoome.databinding.ViewOpeningTimeBinding
import org.helfoome.databinding.ViewSnackbarBinding

class SnackBarView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private var binding: ViewSnackbarBinding
    private lateinit var inflater: LayoutInflater

    init {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(context)
        binding = ViewSnackbarBinding.inflate(inflater, this, true)
    }

    fun setText(snackBarMessage: String) {
        binding.tvSnackBar.text = snackBarMessage
    }
}