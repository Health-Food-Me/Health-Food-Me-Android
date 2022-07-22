package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewImage(
    val id: String,
    val name: String,
    val url: String,
) : Parcelable
