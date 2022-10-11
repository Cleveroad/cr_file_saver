package com.cleveroad.cr_file_saver

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.cleveroad.cr_file_saver.base.AbstractActivityAware
import com.cleveroad.cr_file_saver.base.FileSaverPluginCallback
import com.cleveroad.cr_file_saver.utils.getFileName
import com.cleveroad.cr_file_saver.utils.saveFileInBackground
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.File

/** CrFileSaverPlugin */
class CrFileSaverPlugin : FlutterPlugin, MethodCallHandler, AbstractActivityAware(),
    FileSaverPluginCallback, PluginRegistry.RequestPermissionsResultListener,
    PluginRegistry.ActivityResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var fileSaver: Pigeon.FileSaverApi? = null
    private var activity: Activity? = null
    private var permissionRequestPendingResult: Pigeon.Result<Boolean>? = null

    // Used for file saving through dialog
    private var sourceFile: File? = null
    private var saveFilePendingResult: Pigeon.Result<String>? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "cr_file_saver")
        channel.setMethodCallHandler(this)
        fileSaver = FileSaverImpl(flutterPluginBinding.applicationContext, this)
        Pigeon.FileSaverApi.setup(flutterPluginBinding.binaryMessenger, fileSaver)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${Build.VERSION.RELEASE}")
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        super.onAttachedToActivity(binding)
        activity = binding.activity
        binding.apply {
            addRequestPermissionsResultListener(this@CrFileSaverPlugin)
            addActivityResultListener(this@CrFileSaverPlugin)
        }
    }

    override fun onDetachedFromActivity() {
        super.onDetachedFromActivity()
        activity = null
    }

    override fun onRequestPermission(result: Pigeon.Result<Boolean>?) {
        permissionRequestPendingResult = result
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(FileSaverImpl.PERMISSION),
                PERMISSION_REQCODE
            )
        }
    }

    override fun onSaveFileDialog(sourceFile: File, result: Pigeon.Result<String>?) {
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, getFileName(sourceFile.path))
            type = "*/*"
        }.let {
            this.sourceFile = sourceFile
            this.saveFilePendingResult = result

            activity?.let { activity ->
                // Checking for available activity to handle
                if (it.resolveActivity(activity.packageManager) != null) {
                    activity.startActivityForResult(it, SAVEFILE_REQCODE)
                } else {
                    result?.error(NoResolvedActivityException())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        when (requestCode) {
            PERMISSION_REQCODE -> {
                permissionRequestPendingResult?.success(
                    grantResults.first() == PackageManager.PERMISSION_GRANTED
                )
            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        when (requestCode) {
            SAVEFILE_REQCODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val destinationPath = data.data
                    val contentResolver = activity?.contentResolver
                    if (contentResolver != null && sourceFile != null && destinationPath != null) {
                        saveFileInBackground(
                            contentResolver,
                            sourceFile!!,
                            destinationPath,
                            saveFilePendingResult
                        ) {
                            Log.d(TAG, "Saved file $it via dialog")
                        }
                    }
                }
            }
        }

        return true
    }

    companion object {
        const val PERMISSION_REQCODE = 1
        const val SAVEFILE_REQCODE = 2
        const val TAG = "CrFileSaverPlugin"
    }
}
