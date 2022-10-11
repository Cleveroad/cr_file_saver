package com.cleveroad.cr_file_saver.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.cleveroad.cr_file_saver.Pigeon
import com.cleveroad.cr_file_saver.extensions.toEnvironmentDirectory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


fun getFileName(url: String): String {
    val segments = url.split("/");
    val last = segments.last();
    return last
}

// Only used in old APIs
@Suppress("DEPRECATION")
fun getTargetFile(
    fileName: String,
    destinationDirectory: Pigeon.DestinationDirectory = Pigeon.DestinationDirectory.download
): File {
    return File(
        Environment.getExternalStoragePublicDirectory(destinationDirectory.toEnvironmentDirectory()),
        fileName
    )
}

fun getMimeType(context: Context, path: String): String? {
    val uri = Uri.fromFile(File(path))
    val mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver = context.contentResolver
        cr.getType(uri)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
            uri
                .toString()
        )
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }

    return mimeType
}

fun saveFileInBackground(
    contentResolver: ContentResolver,
    sourceFile: File,
    destinationUri: Uri,
    result: Pigeon.Result<String>?,
    onComplete: ((String?) -> Unit)? = null
) {
    CoroutineScope(Dispatchers.Main).launch {
        val file = withContext(Dispatchers.IO) {
            sourceFile.inputStream().use { input ->
                contentResolver.openOutputStream(destinationUri).use { output ->
                    output?.let { input.copyTo(it) }
                }
            }
            destinationUri.path
        }

        onComplete?.invoke(file)
        result?.success(file)
    }
}