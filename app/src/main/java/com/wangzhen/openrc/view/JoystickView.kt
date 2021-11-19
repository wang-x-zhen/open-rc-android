package com.wangzhen.openrc.view

import com.wangzhen.openrc.utils.UdpUtils.log
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


/**
 *  @author w小z
 *  @since : 2021/4/12
 */
open class JoystickView : View {
    private var xyChange: XYChange? = null
    private var autoBackX: Boolean = false
    private var autoBackY: Boolean = false
    private var touchPointX: Float = 0.0f
    private var touchPointY: Float = 0.0f
    private var padding: Float = 0f
    private var drawTouchPoint = false
    private val circumPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }
    private val touchPaintRelease = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        alpha = 30
    }
    private val touchPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.rgb(70, 175, 255)
        alpha = 100
    }
    private val rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 10f
    }
    private val rectPaintBg = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        alpha = 30
    }
    private val rectWork = Rect(0, 0, 0, 0)

    fun setXYChange(_xyChange: XYChange) {
        xyChange = _xyChange
    }

    fun setAutoBackX(autoBackX: Boolean) {
        this.autoBackX = autoBackX
        init()
    }

    fun setAutoBackY(autoBackY: Boolean) {
        this.autoBackY = autoBackY
        init()
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        log("------onAttachedToWindow-----")
        init()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        log("-----onFinishInflate----")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (rectWork.width() == 0) {
            padding = measuredHeight / 6F
            rectWork.apply {
                left = (0 + padding).toInt()
                right = (measuredHeight - padding).toInt()
                top = (0 + padding).toInt()
                bottom = right
            }
        }
        canvas?.drawCircle(
            measuredWidth.toFloat() / 2,
            measuredWidth.toFloat() / 2,
            measuredWidth.toFloat() / 2,
            circumPaint
        )
        canvas?.drawRect(
            rectWork,
            rectPaintBg
        )
        if (drawTouchPoint) {
            canvas?.drawCircle(
                touchPointX,
                touchPointY,
                measuredWidth.toFloat() / 8,
                touchPaint
            )
        } else {
            canvas?.drawCircle(
                touchPointX,
                touchPointY,
                measuredWidth.toFloat() / 10,
                touchPaintRelease
            )
        }
        drawHLine(canvas)
        drawVLine(canvas)
    }

    private fun init() {
        this.post {
            touchPointX = if (!autoBackX) {
                width - padding
            } else {
                measuredWidth.toFloat() / 2
            }
            touchPointY = if (!autoBackY) {
                width - padding
            } else {
                measuredWidth.toFloat() / 2
            }
            log("-----post touchPointX = $touchPointX----")
            invalidate()
        }
    }

    fun drawRect(canvas: Canvas?) {
        val padding = measuredWidth / 4
        val rect = Rect(
            padding,
            (touchPointY - 20).toInt(),
            measuredWidth - padding,
            (touchPointY + 20).toInt()
        )
        canvas?.drawRect(rect, rectPaint)
    }

    private val hLinePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        strokeWidth = 2f
    }

    private val vLinePaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLUE
        strokeWidth = 2f
    }

    private fun drawHLine(canvas: Canvas?) {
        canvas?.drawLine(
            padding,
            touchPointY,
            measuredWidth.toFloat() - padding,
            touchPointY,
            hLinePaint
        )
    }

    private fun drawVLine(canvas: Canvas?) {
        canvas?.drawLine(
            touchPointX,
            padding,
            touchPointX,
            measuredWidth.toFloat() - padding,
            vLinePaint
        )
    }

    private fun vibrate() {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(20, 20, 20, 20, 20) // 停止 开启 停止 开启
        vib.vibrate(pattern, -1)
    }

    private fun vibrateSmall() {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(10, 10, 10, 10, 10) // 停止 开启 停止 开启
        vib.vibrate(pattern, -1)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                checkIn(event.x, event.y)
                drawTouchPoint = true
                vibrateSmall()
            }
            MotionEvent.ACTION_MOVE -> {
                println("event.x ${event.x} event.y ${event.y}")
                checkIn(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                if (autoBackX) {
                    touchPointX = (measuredWidth / 2).toFloat()
                }
                if (autoBackY) {
                    touchPointY = (measuredWidth / 2).toFloat()
                }
                drawTouchPoint = false
                if (autoBackX || autoBackY) {
                    vibrate()
                }
            }

        }
        val x = (touchPointX - padding) * 2000 / (measuredWidth - 2 * padding)
        val y = (touchPointY - padding) * 2000 / (measuredWidth - 2 * padding)
        xyChange?.onXYChange(x.toInt(), y.toInt())
        log("--------onXYChange----x:$x y:$y---")
        invalidate()
        return true
    }

    private fun checkIn(x: Float, y: Float) {
        touchPointX = if (x < 0 + padding) {
            padding
        } else {
            if (x < width - padding) {
                x
            } else {
                width - padding
            }
        }

        touchPointY = if (y < 0 + padding) {
            padding
        } else {
            if (y < width - padding) {
                y
            } else {
                width - padding
            }
        }
    }

    interface XYChange {
        fun onXYChange(x: Int, y: Int)
    }
}
