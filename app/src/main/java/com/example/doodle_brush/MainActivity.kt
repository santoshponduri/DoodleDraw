package com.example.doodle_brush

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingLayout.setBrushDrawingMode(true)
        brushDraw.setOnClickListener(View.OnClickListener {

            drawingLayout.setBrushDrawingMode(true)
        })


        eraseDraw.setOnClickListener(View.OnClickListener {
            drawingLayout.brushEraser()


        })

        undoDraw.setOnClickListener(View.OnClickListener {
            drawingLayout.undo()

        })
    }
}
