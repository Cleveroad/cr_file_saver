package com.cleveroad.cr_file_saver

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.cleveroad.cr_file_saver.base.FileSaverPluginCallback
import com.cleveroad.cr_file_saver.utils.getFileName
import com.cleveroad.cr_file_saver.utils.getMimeType
import com.cleveroad.cr_file_saver.utils.getTargetFile
import com.cleveroad.cr_file_saver.utils.saveFileInBackground
import java.io.File
import java.net.URL

class FileSaverImpl(private val context: Context, private val callback: FileSaverPluginCallback) :
    Pigeon.FileSaverApi {

    override fun saveFile(
            filePath: String,
//        directory: Pigeon.DestinationDirectory,
            destinationFileName: String?,
            result: Pigeon.Result<String>?
    ) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            saveFileBeforeSDK29(filePath, destinationFileName, result)
        } else {
            saveFileAfterSDK29(filePath, destinationFileName, result)
        }
    }

    override fun requestWriteExternalStoragePermission(result: Pigeon.Result<Boolean>?) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

        // Before Android 6 (in-app permissions)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            result?.success(granted)
            return
            // Before Android 10 (Scoped storage that does not require permission)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            if (granted) {
                result?.success(true)
            } else {
                callback.onRequestPermission(result)
            }
        } else {
            /// After Android 10 (inclusive)
            result?.success(true)
        }
    }

    override fun saveFileWithDialog(
        params: Pigeon.SaveFileDialogParams,
        result: Pigeon.Result<String>?
    ) {
        val sourceFile = File(params.sourceFilePath)
        callback.onSaveFileDialog(sourceFile, result)
    }

    @Suppress("DEPRECATION")
    private fun saveFileBeforeSDK29(
        url: String,
        destinationFileName: String?,
//        directory: Pigeon.DestinationDirectory,
        result: Pigeon.Result<String>?
    ) {
        Log.d(TAG, "saveFileBeforeSDK29: Saving $url")
        try {
            val fileName = getFileName(url)
            val target = Uri.fromFile(getTargetFile(destinationFileName ?: fileName))

            saveFileInBackground(context.contentResolver, File(url), target, result) {
                Log.i(TAG, "saveFileBeforeSDK29: Saved file as ${target.path}")
            }

        } catch (error: Throwable) {
            Log.e(TAG, "saveFileBeforeSDK29", error)

            result?.error(error)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileAfterSDK29(
        path: String,
        destinationFileName: String?,
//        directory: Pigeon.DestinationDirectory,
        result: Pigeon.Result<String>?
    ) {
        try {
            val mimeType = getMimeType(context, path)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, destinationFileName ?: getFileName(path))
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                val url = Uri.fromFile(File(path)).toString()
                URL(url).openStream().use { input ->
                    resolver.openOutputStream(uri).use { output ->
                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                    }
                }

                Log.i(TAG, "saveFileAfterSDK29: Saved file as ${uri.path}")
                result?.success(uri.path)
            } ?: result?.error(Exception("uri == null"))
        } catch (exception: Throwable) {
            Log.e(TAG, "saveFileAfterSDK2", exception)
            result?.error(exception)
        }
    }

    companion object {
        const val TAG = "FileSaverImpl"
        const val PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}