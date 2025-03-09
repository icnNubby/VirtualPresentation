package com.example.display_id_protocol

import android.content.ContentResolver
import android.net.Uri
import com.example.display_id_protocol.Constants.DISPLAY_ID_PARAMETER
import com.example.display_id_protocol.Constants.AUTHORITY

fun createStartPresentationUri(displayId: Int): Uri =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .path(Constants.START_PRESENTATION_ACTION)
        .appendQueryParameter(DISPLAY_ID_PARAMETER, displayId.toString())
        .build()

fun createStopPresentationUri(): Uri =
    Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(AUTHORITY)
        .path(Constants.STOP_PRESENTATION_ACTION)
        .build()