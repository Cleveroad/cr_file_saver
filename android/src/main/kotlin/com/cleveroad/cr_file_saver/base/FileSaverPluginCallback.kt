package com.cleveroad.cr_file_saver.base

import android.content.Context
import com.cleveroad.cr_file_saver.Pigeon
import java.io.File

interface FileSaverPluginCallback {
    fun onRequestPermission(result: Pigeon.Result<Boolean>?)
    fun onSaveFileDialog(
        context: Context,
        sourceFile: File,
        result: Pigeon.Result<String>?,
        destinationFileName: String?
    )
}