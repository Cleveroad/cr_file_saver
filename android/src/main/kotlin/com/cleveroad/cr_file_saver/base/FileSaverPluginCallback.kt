package com.cleveroad.cr_file_saver.base

import com.cleveroad.cr_file_saver.Pigeon
import java.io.File

interface FileSaverPluginCallback {
    fun onRequestPermission(result: Pigeon.Result<Boolean>?)
    fun onSaveFileDialog(sourceFile: File, result: Pigeon.Result<String>?)
}