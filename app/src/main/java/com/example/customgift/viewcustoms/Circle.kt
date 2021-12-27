package com.example.customgift.viewcustoms

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import kotlinx.coroutines.*
import kotlin.math.abs

class Circle(
    val pos: Int = 0,
    private val numCircles: Int = 5,
    private val colorCircle: Int = Color.GREEN,
    context: Context
) :
    View(context) {
    private var mX: Int = 0
    var mY: Int = 0

    private val speed = DEFAULT_SPEED

    private var callBack: ((Int) -> Unit)? = null
    private val mPaintCircle = Paint().apply {
        color = colorCircle
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10F
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }
    private var radius: Float = 0F
    private var initCoordinatesX = 0
    private var initCoordinatesY = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidth = MeasureSpec.getSize(widthMeasureSpec) / numCircles
        val mHeight = MeasureSpec.getSize(heightMeasureSpec)
        initCoordinatesY = mHeight / 2
        initCoordinatesX = mWidth / 2
        radius = mWidth / 5F
        mX = initCoordinatesX
        mY = initCoordinatesY
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(mX.toFloat(), mY.toFloat(), radius, mPaintCircle)
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (pos == 0) {
            when (event?.action) {
                ACTION_DOWN -> {
                    Log.e("size", event.y.toString())
                    job?.cancel()
                }

                ACTION_MOVE -> {
                    mY = event.y.toInt()
                    callBack?.invoke(mY)
                    invalidate()
                }

                ACTION_UP -> {
                    reset(event.y.toInt())
                }
            }
            return true
        } else {
            return true
        }
    }

    private var job: Job? = null
    private fun reset(y: Int) {
        val direction = if (y > initCoordinatesY) -1 else 1
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(TIME_RESET_DELAY)
            while (abs(mY - initCoordinatesY) >= speed) {
                mY += speed * direction
                callBack?.invoke(mY)
                delay(TIME_STEP_DELAY)
            }
            mY += abs(mY - initCoordinatesY) * direction
            callBack?.invoke(mY)
        }
    }

    fun setCallback(action: (Int) -> Unit) {
        callBack = action
    }

    fun moveCircles(newPos: Int) {
        mY = newPos
    }

    companion object {
        const val TIME_RESET_DELAY = 3000L
        const val TIME_STEP_DELAY = 10L
        const val DEFAULT_SPEED = 20
    }
}