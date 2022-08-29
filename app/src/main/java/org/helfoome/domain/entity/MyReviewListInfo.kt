package org.helfoome.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyReviewListInfo( // TODO MyReviewInfo 로 data class명 수정
    val id: String,
    val restaurant: String,
    val score: Float,
    val tags: String, // TODO 프로퍼티명 수정
    val good: List<String>,
    val description: String,
    val photoList: List<ReviewImage>,
) : Parcelable
