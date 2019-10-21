package com.example.doodle_brush

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import java.util.*

class BrushView : View {

    internal val DEFAULT_BRUSH_SIZE = 40.0f
    internal val DEFAULT_ERASER_SIZE = 50.0f
    internal val DEFAULT_OPACITY = 255
    private var addedViews: MutableList<View>? = null
    private var redoViews: MutableList<View>? = null
    private var mBrushSize = DEFAULT_BRUSH_SIZE
    private var mBrushEraserSize = DEFAULT_ERASER_SIZE

    private val mDrawnPaths = Stack<DrawingPath>()
    private val mRedoPaths = Stack<DrawingPath>()
    private val mDrawPaint = Paint()

    private var mDrawCanvas: Canvas? = null
    private var mBrushDrawMode: Boolean = false

    private lateinit var mPath: Path
    private var mTouchX: Float = 0.toFloat()
    private var mTouchY: Float = 0.toFloat()
    private val TOUCH_TOLERANCE = 4f

    constructor(context: Context) : super(context) {
        setupBrushDrawing()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupBrushDrawing()
    }


    private fun setupBrushDrawing() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mDrawPaint.color = Color.WHITE
        setupPathAndPaint()
        setVisibility(View.GONE)

        addedViews = ArrayList()
        redoViews = ArrayList()
    }

    private fun setupPathAndPaint() {
        mPath = Path()
        mDrawPaint.isAntiAlias = true
        mDrawPaint.isDither = true
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.strokeWidth = mBrushSize
        mDrawPaint.alpha = DEFAULT_OPACITY
        mDrawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    private fun refreshBrushDrawing() {
        mBrushDrawMode = true
        setupPathAndPaint()
    }

    fun brushEraser() {
        mBrushDrawMode = true
        mDrawPaint.strokeWidth = mBrushEraserSize
        mDrawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setBrushDrawingMode(brushDrawMode: Boolean) {
        this.mBrushDrawMode = brushDrawMode
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE)
            refreshBrushDrawing()
        }
    }

    fun setBrushSize(size: Float) {
        mBrushSize = size
        setBrushDrawingMode(true)
    }

    fun setBrushColor(@ColorInt color: Int) {
        mDrawPaint.color = color
        setBrushDrawingMode(true)
    }

    fun setBrushEraserSize(brushEraserSize: Float) {
        this.mBrushEraserSize = brushEraserSize
        mDrawPaint.strokeWidth = mBrushEraserSize
        // setBrushDrawingMode(true);
    }


    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mDrawCanvas = Canvas(canvasBitmap)
    }

    protected override fun onDraw(canvas: Canvas) {
        for (linePath in mDrawnPaths) {
            canvas.drawPath(linePath.getDrawPath(), linePath.getDrawPaint())
        }
        canvas.drawPath(mPath!!, mDrawPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mBrushDrawMode) {
            val touchX = event.x
            val touchY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchStart(touchX, touchY)
                MotionEvent.ACTION_MOVE -> touchMove(touchX, touchY)
                MotionEvent.ACTION_UP -> touchUp()
            }
            invalidate()
            return true
        } else {
            return false
        }
    }

    fun undo(): Boolean {
        if (!mDrawnPaths.empty()) {
            mRedoPaths.push(mDrawnPaths.pop())
            invalidate()
        }


        if (addedViews!!.size > 0) {
            val removeView = addedViews!!.removeAt(addedViews!!.size - 1)

            redoViews!!.add(removeView)
        }

        return !mDrawnPaths.empty()
    }

    internal fun redo(): Boolean {
        if (!mRedoPaths.empty()) {
            mDrawnPaths.push(mRedoPaths.pop())
            invalidate()
        }

        if (redoViews!!.size > 0) {
            redoViews!!.removeAt(redoViews!!.size - 1)
        }
        addedViews!!.add(this)


        return !mRedoPaths.empty()
    }


    private fun touchStart(x: Float, y: Float) {
        mRedoPaths.clear()
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mTouchX = x
        mTouchY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mTouchX)
        val dy = Math.abs(y - mTouchY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2)
            mTouchX = x
            mTouchY = y
        }
    }

    private fun touchUp() {
        mPath?.lineTo(mTouchX, mTouchY)
        mPath?.let { mDrawCanvas?.drawPath(it, mDrawPaint) }

        mDrawnPaths.push(DrawingPath(mPath, mDrawPaint))
        mPath = Path()

        if (redoViews!!.size > 0) {
            redoViews!!.removeAt(redoViews!!.size - 1)
        }
        addedViews!!.add(this)

    }

}