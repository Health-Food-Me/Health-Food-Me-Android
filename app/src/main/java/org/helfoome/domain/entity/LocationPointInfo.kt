package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPointInfo(
    val lat: Double,
    val lng: Double,
) : Parcelable
