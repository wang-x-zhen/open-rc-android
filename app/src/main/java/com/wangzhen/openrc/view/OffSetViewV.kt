package com.wangzhen.openrc.view

import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


/**
 *  @author w小z
 *  @since : 2021/4/23
 */
open class OffSetViewV : View {

    private val rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4f
    }
    private var offsetChange: OffsetChange? = null
    fun setOffsetChange(offsetChange: OffsetChange) {
        this.offsetChange = offsetChange
    }

    private var rect = Rect(
        0,
        0,
        0,
        0
    )

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawRect(canvas)
        drawVLine(canvas)
    }

    private fun drawRect(canvas: Canvas?) {
        if (rect.width() == 0) {
            rect = Rect(
                0,
                0,
                measuredWidth,
                measuredHeight
            )
        }
        canvas?.drawRect(rect, rectPaint)
    }

    private val vLinePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        strokeWidth = 2f
    }

    private var lastYLine = 0
    private var offsetLine = 0
    private val lineCount = 10
    private fun drawVLine(canvas: Canvas?) {
        val perY = measuredHeight / lineCount
        if (lastYLine != 0) {
            offsetLine = (abs(lastYLine - moveY) % perY).toInt()
        }
        for (x in 0..lineCount) {
            canvas?.drawLine(
                0F,
                moveY - x * perY + offsetLine,
                measuredHeight.toFloat(),
                moveY - x * perY + offsetLine,
                vLinePaint
            )
        }
        for (x in 0..lineCount) {
            canvas?.drawLine(
                0F,
                moveY + x * perY + offsetLine,
                measuredHeight.toFloat(),
                moveY + x * perY + offsetLine,
                vLinePaint
            )
        }
        canvas?.drawLine(
            0F,
            moveY + offsetLine,
            measuredWidth.toFloat(),
            moveY + offsetLine,
            vLinePaint
        )
        lastYLine = moveY.toInt()
    }

    private fun vibrate() {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(3, 3, 3) // 停止 开启 停止 开启
        vib.vibrate(pattern, -1)
    }

    var startY = 0F
    var moveY = 0F
    var vLastY = 0
    var lastY = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                    return true
                }
                moveY = event.y
                offsetChange?.onOffsetChange(-1 * (moveY - lastY).toInt())
                lastY = moveY
                if (moveY.toInt() % 5 == 0 && vLastY != moveY.toInt()) {
                    vLastY = moveY.toInt()
                    vibrate()
                }

            }
            MotionEvent.ACTION_UP -> {
            }

        }
        invalidate()
        return true
    }

    interface OffsetChange {
        fun onOffsetChange(x: Int)
    }

}
