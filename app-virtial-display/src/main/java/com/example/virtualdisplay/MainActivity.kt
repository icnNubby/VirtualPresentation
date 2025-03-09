package com.example.virtualdisplay

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
import android.hardware.display.DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
import android.hardware.display.VirtualDisplay
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.display_id_protocol.createStartPresentationUri
import com.example.display_id_protocol.createStopPresentationUri

class MainActivity : AppCompatActivity() {

    private val displayManager by lazy { getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }
    private val surface by lazy { findViewById<SurfaceView>(R.id.surface) }
    private val start by lazy { findViewById<Button>(R.id.start_presentation) }
    private val stop by lazy { findViewById<Button>(R.id.stop_presentation) }

    private var virtualDisplay: VirtualDisplay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start.setOnClickListener { startPresentation() }
        stop.setOnClickListener { stopPresentation() }
    }

    private fun startPresentation() {
        if (virtualDisplay == null) {
            virtualDisplay = createVirtualDisplay()
        }
        val uri = createStartPresentationUri(virtualDisplay!!.display.displayId)
        Log.i(TAG, "Starting presentation with uri: $uri")
        contentResolver.query(uri, null, null, null)?.close()
    }

    private fun stopPresentation() {
        virtualDisplay?.surface = null
        virtualDisplay?.release()
        val uri = createStopPresentationUri()
        Log.i(TAG, "Stopping presentation with uri: $uri")
        contentResolver.query(uri, null, null, null)?.close()
        virtualDisplay = null
    }

    private fun createVirtualDisplay(): VirtualDisplay {
        return displayManager.createVirtualDisplay(
            "Example Virtual Display",
            surface.width,
            surface.height,
            resources.displayMetrics.densityDpi,
            surface.holder.surface,
            VIRTUAL_DISPLAY_FLAG_PUBLIC or VIRTUAL_DISPLAY_FLAG_PRESENTATION or VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
            null,
            null,
        )
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}