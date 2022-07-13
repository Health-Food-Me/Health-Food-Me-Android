package org.helfoome.util

import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import org.helfoome.R

object ChipFactory {
    fun create(layoutInflater: LayoutInflater): Chip =
        layoutInflater.inflate(R.layout.view_chip_food, null, false) as Chip
}
