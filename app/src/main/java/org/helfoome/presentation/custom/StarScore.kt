package org.helfoome.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.helfoome.R
import org.helfoome.databinding.ViewRatingBarBinding
import timber.log.Timber

class StarScore(context: Context, val attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var binding: ViewRatingBarBinding
    private lateinit var inflater: LayoutInflater

    init {
        initView()
        initAttr()
    }

    private fun initView() {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(context)
        binding = ViewRatingBarBinding.inflate(inflater, this, true)
    }

    private fun initAttr() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)
        val score = typedArray.getFloat(R.styleable.RatingBar_score, 0f)
        setScore(score)

        typedArray.recycle()
    }

    fun setScore(score: Float) {
        if (score in 0f..5f) setStarImage(score)
        else Timber.d("Invalid Score: Error")
    }

    private fun setStarImage(score: Float) {
        with(binding) {
            val starViewList = listOf(ivStar1, ivStar2, ivStar3, ivStar4, ivStar5)

            // '2.5'과 같이 인소수점을 기준으로 정수와, 소수점 첫째자리 수를 가져옴(단, n.m 포맷의 실수여야함)
            // 정수에 해당하는 아이콘 디스플레이
            val integer = score.toString()[0].digitToIntOrNull() ?: return
            val firstDecimalPlace = score.toString()[2].digitToIntOrNull() ?: return
            var lastIdx = 0
            for (i in 0 until integer) {
                starViewList[i].setImageResource(R.drawable.ic_star_full)
                lastIdx = i
            }

            // 소수점 첫째자리에 해당하는 아이콘 디스플레이
            starViewList[lastIdx + 1].setImageResource(getDecimalPointImageRes(firstDecimalPlace) ?: return)
        }
    }

    private fun getDecimalPointImageRes(score: Int): Int? {
        return when (score) {
            in 1..4 -> R.drawable.ic_star_30_per
            5 -> R.drawable.ic_star_50_per
            in 6..9 -> R.drawable.ic_star_70_per
            else -> null
        }
    }
}
