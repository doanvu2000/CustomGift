package com.example.customgift.viewcustoms

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private val colorCircle: Int = Color.RED,
    context: Context
) :
    View(context) {

    private var x: Int = 0
    var mY: Int = 0
    private var job: Job? = null
    private val speed = 20
    private var radius: Float = 0F
    private var initCoordinatesX = 0
    private var initCoordinatesY = 0

    private var callBack: ((Int) -> Unit)? = null

    fun setCallback(action: (Int) -> Unit) {
        callBack = action
    }


    override fun onDraw(canvas: Canvas?) {
        drawCircle(canvas)
    }

    private val mPaintCircle = Paint().apply {
        color = colorCircle
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10F
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(x.toFloat(), mY.toFloat(), radius, mPaintCircle)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidth = MeasureSpec.getSize(widthMeasureSpec) / numCircles
        val mHeight = MeasureSpec.getSize(heightMeasureSpec)
        initCoordinatesY = mHeight / 2
        initCoordinatesX = mWidth / 2
        radius = mWidth / 5F
        x = initCoordinatesX
        mY = initCoordinatesY
        setMeasuredDimension(mWidth, mHeight)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (pos == 0) {
            when (event?.action) {
                ACTION_DOWN -> {
                    job?.cancel()
                }

                ACTION_MOVE -> {
                    mY = event.y.toInt()
                    callBack?.invoke(mY)
                    invalidate()
                }

                ACTION_UP -> {
                    clear(event.y.toInt())
                }
            }
            return true
        } else {
            return true
        }
    }


    private fun clear(y: Int) {
        val direction = if (y > initCoordinatesY) -1 else 1
        job = CoroutineScope(Dispatchers.Default).launch {
            delay(5000L)
            while (abs(mY - initCoordinatesY) >= speed) {
                mY += speed * direction
                callBack?.invoke(mY)
                delay(10L)
            }
            mY += abs(mY - initCoordinatesY) * direction
            callBack?.invoke(mY)
        }
    }

    fun moveCircles(y: Int) {
        mY = y
    }

}