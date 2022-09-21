package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScrapInfo(
    val id: String,
    val restaurantImg: String,
    val title: String,
    val location: String,
    val isBookmarked: Boolean,
    val latitude: Double,
    val longitude: Double,
    val score: Double,
) : Parcelable
