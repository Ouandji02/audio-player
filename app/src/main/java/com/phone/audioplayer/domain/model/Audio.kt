package com.phone.audioplayer.domain.model

import android.net.Uri

data class Audio(
    val uri : Uri,
    val displayName : String,
    val id : Long,
    val title : String,
    val artist : String,
    val duration : Int,
    val data : String
)
