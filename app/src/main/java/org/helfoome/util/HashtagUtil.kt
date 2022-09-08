package org.helfoome.util

import android.content.Context
import org.helfoome.presentation.type.GoodPointHashtagType
import org.helfoome.presentation.type.TasteHashtagType

class HashtagUtil(private val context: Context) {
    private val tasteTags = TasteHashtagType.values().map { it.strRes to it }
    private val goodTags = GoodPointHashtagType.values().map { it.strRes to it }

    fun convertStrToTasteTag(str: String): TasteHashtagType? {
        if (tasteTags.size < 3) return null
        return when (str) {
            context.getString(tasteTags[0].first) -> tasteTags[0].second
            context.getString(tasteTags[1].first) -> tasteTags[1].second
            else -> tasteTags[2].second
        }
    }

    fun convertStrToGoodTag(str: String): GoodPointHashtagType? {
        if (goodTags.size < 3) return null
        return when (str) {
            context.getString(goodTags[0].first) -> goodTags[0].second
            context.getString(goodTags[1].first) -> goodTags[1].second
            else -> goodTags[2].second
        }
    }
}
