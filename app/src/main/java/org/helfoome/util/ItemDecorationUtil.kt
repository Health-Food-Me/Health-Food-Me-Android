package org.helfoome.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

sealed class ItemDecorationUtil {
    class ItemDecoration(
        private val height: Float,
        private val dividerPadding: Float?,
        @ColorInt
        private val color: Int,
        private val padding: Int,
        private val isVertical: Boolean = true,
    ) : RecyclerView.ItemDecoration() {
        private val paint = Paint()

        init {
            paint.color = color
        }

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            when (parent.getChildAdapterPosition(view)) {
                0 -> {
                    with(outRect) {
                        if (!isVertical) {
                            left = padding
                            right = padding
                        } else {
                            top = padding
                            bottom = padding
                        }
                    }
                }
                else -> {
                    if (!isVertical) {
                        outRect.right = padding
                    } else {
                        outRect.bottom = padding
                    }
                }
            }
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (dividerPadding == null) return
            val start = parent.paddingStart + dividerPadding
            val end = parent.width - parent.paddingEnd - dividerPadding

            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams

                val top = (child.bottom + params.bottomMargin).toFloat()
                val bottom = top + height

                c.drawRect(start, top, end.toFloat(), bottom, paint)
            }
        }
    }
}
