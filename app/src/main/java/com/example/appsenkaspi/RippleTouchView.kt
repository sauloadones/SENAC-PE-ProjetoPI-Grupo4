package com.example.appsenkaspi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.animation.ValueAnimator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.LinearInterpolator

class RippleTouchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val ripples = mutableListOf<Ripple>()
    private val paint = Paint().apply {
        color = 0x55FFFFFF.toInt() // semi-transparente branco
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            startRipple(event.x, event.y)
        }
        return true
    }

    private fun startRipple(x: Float, y: Float) {
        val ripple = Ripple(PointF(x, y))
        ripples.add(ripple)
        ripple.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (ripple in ripples.toList()) { // cópia da lista para evitar ConcurrentModification
            ripple.draw(canvas)
        }
    }

    private inner class Ripple(private val center: PointF) {
        private var radius = 0f
        private var alpha = 255

        private val animator = ValueAnimator.ofFloat(0f, 80f).apply {
            duration = 600
            interpolator = LinearInterpolator()
            addUpdateListener {
                radius = it.animatedValue as Float
                alpha = (255 * (1f - it.animatedFraction)).toInt()
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    ripples.remove(this@Ripple)
                }
            })
        }

        fun start() {
            animator.start()
        }

        fun draw(canvas: Canvas) {
            paint.alpha = alpha
            canvas.drawCircle(center.x, center.y, radius, paint)
        }
    }
}
