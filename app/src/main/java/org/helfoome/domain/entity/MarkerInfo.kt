package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkerInfo(
    val id: String,
    val isDietRestaurant: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String,
) : Parcelable
