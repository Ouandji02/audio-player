package com.phone.audioplayer.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.phone.audioplayer.domain.model.Audio

class ContentResolverHelper constructor(context: Context) {
    private var mCursor: Cursor? = null
    private val contextApplicaion = context
    private var mProjection = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA
    )
    private val selectionClause: String = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
    private val selectionArgs = arrayOf("1")
    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @WorkerThread
    fun getAudioData(): List<Audio> {
            return getCursorData()
    }

    private fun getCursorData(): MutableList<Audio> {
        var audioList = mutableListOf<Audio>()
        mCursor = contextApplicaion.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mProjection,
            selectionClause,
            selectionArgs,
            sortOrder
        )

        mCursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

            it.apply {
                if (count == 0) println("IL YA PAS D'AUDIO")
                else {
                    while (it.moveToNext()) {
                        val id = getLong(idColumn)
                        val displayName = getString(displayNameColumn)
                        val artist = getString(artistColumn)
                        val title = getString(titleColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        audioList += Audio(
                            uri, displayName, id, title, artist, duration, data
                        )
                    }
                }
            }
        }
        return audioList
    }
}