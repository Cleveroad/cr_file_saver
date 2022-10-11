import 'dart:io';

import 'package:cr_file_saver/src/pigeon.dart';

class CRFileSaver {
  CRFileSaver._();

  /// In Android 5 return result of `ContextCompat.checkSelfPermission`
  /// Firstly, you need add permission into AndroidManifest.xml
  ///
  /// `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
  ///
  /// In Android 6-9 will be showed permission request dialog
  /// In Android 10 and later nothing won't be showed because Scoped Storage doesn't
  /// require permission
  ///
  /// On ios platform always returns true
  static Future<bool> requestWriteExternalStoragePermission() async {
    if (Platform.isIOS) {
      return true;
    }
    final granted =
        await FileSaverApi().requestWriteExternalStoragePermission();
    return granted ?? false;
  }

  /// Silent saving file into:
  /// * Downloads directory (only Android)
  /// * Document folder (only IOS) - downloaded files will be showed in Files App on iOS
  ///
  /// Parameters:
  /// * [filePath] - absolute source file path.
  /// * [destinationFileName] - file name with extension (e.g. file.pdf)
  static Future<String?> saveFile(
    String filePath, {
    String? destinationFileName,
    // DestinationDirectory destinationDirectory = DestinationDirectory.download,
  }) {
    return FileSaverApi().saveFile(
      filePath,
      // destinationDirectory,
      destinationFileName,
    );
  }

  /// Save file through standard file saving dialog
  ///
  /// But you should consider:
  ///
  /// 1. For using this method you shouldn't specify android.permission.WRITE_EXTERNAL_STORAGE (only android)
  /// 2. On Android if your phone can't handle `Intent.ACTION_CREATE_DOCUMENT`
  /// (e.g. devices with api 30 or higher)
  /// you will receive an exception with code [NoResolvedActivityException]
  static Future<String?> saveFileWithDialog(SaveFileDialogParams params) {
    return FileSaverApi().saveFileWithDialog(params);
  }
}
