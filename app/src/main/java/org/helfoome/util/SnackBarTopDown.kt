package org.helfoome.util

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import org.helfoome.R
import org.helfoome.presentation.custom.SnackBarView

object SnackBarTopDown {
    fun makeSnackBarTopDown(context: Context, snackBarView: SnackBarView, snackBarText: String) {

        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_snackbar_top_down)

        snackBarView.animation = animation
        snackBarView.setText(snackBarText)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                val bottomTopAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_snackbar_bottom_top)
                snackBarView.animation = bottomTopAnimation
                snackBarView.setText(snackBarText)
            }

            override fun onAnimationRepeat(p0: Animation?) = Unit
        })
    }
}
