package com.example.doodle_brush

import android.graphics.Paint
import android.graphics.Path

class DrawingPath {

    private lateinit var mDrawPaint: Paint
    private lateinit var mDrawPath: Path

    constructor (drawPath: Path, drawPaints: Paint) {
        this.mDrawPaint = Paint(drawPaints)
        mDrawPath = Path(drawPath)
    }

    fun getDrawPaint(): Paint {
        return mDrawPaint
    }

    fun getDrawPath(): Path {
        return mDrawPath
    }
}