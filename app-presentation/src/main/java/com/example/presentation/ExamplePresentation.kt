package com.example.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ExamplePresentation(context: Context, display: Display): Presentation(context, display) {

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = Runnable {
        updateText()
        scheduleNextUpdate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        setContentView(R.layout.presentation_layout)
        updateText()
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
        scheduleNextUpdate()
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        stopUpdate()
    }

    private fun scheduleNextUpdate() {
        mainThreadHandler.postDelayed(timerRunnable, 1000L)
    }

    private fun stopUpdate() {
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun updateText() {
        findViewById<TextView>(R.id.presentation_text).text =
            context.getString(
                R.string.presentation_text,
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            )
    }

    companion object {
        private const val TAG = "ExamplePresentation"
    }
}