package com.example.presentation

import android.app.Presentation
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.display_id_protocol.Constants
import com.example.display_id_protocol.Constants.DISPLAY_ID_PARAMETER
import com.example.display_id_protocol.Constants.START_PRESENTATION_ACTION
import com.example.display_id_protocol.Constants.STOP_PRESENTATION_ACTION

class PresentationContentProvider: ContentProvider() {

    private val displayManager by lazy {
        context!!.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private val uriMatcher by lazy {
        UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, START_PRESENTATION_ACTION, TYPE_START_PRESENTATION)
            addURI(AUTHORITY, STOP_PRESENTATION_ACTION, TYPE_STOP_PRESENTATION)
        }
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var presentation: Presentation? = null

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query $uri")
        when (uriMatcher.match(uri)) {
            TYPE_START_PRESENTATION -> {
                mainThreadHandler.post {
                    Log.d(TAG, "starting presentation...")
                    if (presentation == null) {
                        val displayId = uri.getQueryParameter(DISPLAY_ID_PARAMETER)?.toInt() ?: run {
                            Log.e(TAG, "Failed to show presentation, no displayId")
                        }
                        presentation = createPresentation(displayId)
                        try {
                            presentation!!.show()
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to show presentation $e")
                        }
                    }
                }
            }
            TYPE_STOP_PRESENTATION -> {
                mainThreadHandler.post {
                    Log.d(TAG, "stopping presentation...")
                    presentation?.dismiss()
                    presentation = null
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        return null
    }

    private fun createPresentation(displayId: Int): Presentation {
        val display = displayManager.getDisplay(displayId)
        return ExamplePresentation(context!!, display)
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    companion object {
        private const val TAG = "PresentationContentProvider"

        private const val AUTHORITY = Constants.AUTHORITY
        private const val TYPE_START_PRESENTATION = 100
        private const val TYPE_STOP_PRESENTATION = 200
    }
}