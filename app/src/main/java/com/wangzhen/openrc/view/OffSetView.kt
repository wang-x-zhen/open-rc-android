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
open class OffSetView : View {

    private val rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
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
        color = Color.WHITE
        strokeWidth = 2f
    }

    private var lastXLine = 0
    private var offsetLine = 0
    private val lineCount = 10
    private fun drawVLine(canvas: Canvas?) {
        val perX = measuredWidth / lineCount
        if (lastXLine != 0) {
            offsetLine = (abs(lastXLine - moveX) % perX).toInt()
        }
        for (x in 0..lineCount) {
            canvas?.drawLine(
                moveX - x * perX + offsetLine,
                0F,
                moveX - x * perX + offsetLine,
                measuredHeight.toFloat(),
                vLinePaint
            )
        }
        for (x in 0..lineCount) {
            canvas?.drawLine(
                moveX + x * perX + offsetLine,
                0F,
                moveX + x * perX + offsetLine,
                measuredHeight.toFloat(),
                vLinePaint
            )
        }
        canvas?.drawLine(
            moveX + offsetLine,
            0F,
            moveX + offsetLine,
            measuredHeight.toFloat(),
            vLinePaint
        )
        lastXLine = moveX.toInt()
    }

    private fun vibrate() {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(3, 3, 3) // 停止 开启 停止 开启
        vib.vibrate(pattern, -1)
    }

    var startX = 0F
    var moveX = 0F
    var vLastX = 0
    var lastX = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                lastX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                if (!rect.contains(event.x.toInt(), event.y.toInt())) {
                    return true
                }
                moveX = event.x
                offsetChange?.onOffsetChange((moveX - lastX).toInt())
                lastX = moveX
                if (moveX.toInt() % 5 == 0 && vLastX != moveX.toInt()) {
                    vLastX = moveX.toInt()
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
