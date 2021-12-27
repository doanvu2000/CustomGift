package com.example.customgift.viewcustoms

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import com.example.customgift.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Bouncy : GridLayout {

    private var listCircles = mutableListOf<Circle>()

    companion object {
        const val TIME_CHAIN_STEP_DELAY = 100L
        const val DEFAULT_CIRCLES_COUNT = 5
        val listColor = mutableListOf(Color.BLUE, Color.GREEN, Color.BLACK, Color.GRAY, Color.RED, Color.YELLOW)
        var count = 0
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        var circlesCount = 0
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.Bouncy,
            0, 0
        ).apply {
            try {
                circlesCount = getInt(R.styleable.Bouncy_circlesCount, DEFAULT_CIRCLES_COUNT)
                columnCount = circlesCount
                rowCount = 1
            } finally {
                recycle()
            }
        }
        initCircles(circlesCount)
    }

    private fun initCircles(circleCounts: Int) {
        for (i in 0 until circleCounts) {
            val circle = Circle(i, circleCounts, getRandomColor(), context)
            addView(circle)
            listCircles.add(circle)
        }

        //auto bouncy circle
        val handler = Handler(Looper.getMainLooper())
        val handler2 = Handler(Looper.getMainLooper())
        val top = 500
        val bottom = 1500
        val run = object :Runnable{
            override fun run() {
                handler2.postDelayed({
                    listCircles.forEach { circle ->
                        startChainBouncy(circle, top)
                    }
//                    startChainBouncy(listCircles[1], top)
                },1000)
                handler2.postDelayed({
                    listCircles.forEach { circle ->
                        startChainBouncy(circle, bottom)
                    }
//                    startChainBouncy(listCircles[1], bottom)
                },500)
                postDelayed(this,1500)
            }
        }
        handler.postDelayed(run,1500)
        startChainBouncy(listCircles[1],top)
        startChainBouncy(listCircles[1],bottom)
        Log.d("TAG", "initCircles: $count")
        //drag circle
        listCircles[0].setCallback { pos ->
            listCircles.forEach { circle ->
                startChainBouncy(circle, pos)
            }
            handler.removeCallbacks(run)
        }
    }

    private fun startChainBouncy(circle: Circle, newY: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(TIME_CHAIN_STEP_DELAY * circle.pos)
            circle.moveCircles(newY)
//            if (circle.pos > 0) {
//                circle.moveCircles(newY)
//            }
        }
    }

    private fun getRandomColor(): Int {
        listColor.shuffle()
        return listColor[0]
    }

}