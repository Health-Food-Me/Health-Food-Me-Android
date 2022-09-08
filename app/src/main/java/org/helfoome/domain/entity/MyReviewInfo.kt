package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyReviewInfo(
    val id: String,
    val restaurant: String,
    val score: Float,
    val taste: String,
    val good: List<String>,
    val description: String,
    val photoList: List<ReviewImage>,
) : Parcelable
