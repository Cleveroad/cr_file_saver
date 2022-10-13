package com.cleveroad.cr_file_saver.extensions

import android.os.Environment
import com.cleveroad.cr_file_saver.Pigeon

// Currently not used
fun Pigeon.DestinationDirectory.toEnvironmentDirectory(): String = when (this) {
    Pigeon.DestinationDirectory.download -> Environment.DIRECTORY_DOWNLOADS
    Pigeon.DestinationDirectory.dcim -> Environment.DIRECTORY_DCIM
    Pigeon.DestinationDirectory.movies -> Environment.DIRECTORY_MOVIES
    Pigeon.DestinationDirectory.music -> Environment.DIRECTORY_MUSIC
    Pigeon.DestinationDirectory.podcasts -> Environment.DIRECTORY_PODCASTS
    Pigeon.DestinationDirectory.documents -> Environment.DIRECTORY_DOCUMENTS
    Pigeon.DestinationDirectory.images -> Environment.DIRECTORY_PICTURES
}