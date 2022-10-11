import 'dart:developer';
import 'dart:io';

import 'package:cr_file_saver/file_saver.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:path_provider/path_provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const _tempFileName = 'TempFile.pdf';
  static const _testFileName = 'TestFile.pdf';
  static const _testWithDialogFileName = 'TestFileWithDialog.pdf';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: FutureBuilder<bool>(
            future: _checkIsTempFileExists(),
            builder: (context, payload) {
              return Column(
                children: [
                  OutlinedButton(
                    onPressed: _createTempPressed,
                    child: const Text('Create temp file'),
                  ),
                  OutlinedButton(
                    onPressed: _onCheckPermissionPressed,
                    child: const Text('Check permission'),
                  ),
                  if (payload.data == true) ...[
                    OutlinedButton(
                      onPressed: _onSaveFilePressed,
                      child: const Text('Save file'),
                    ),
                    OutlinedButton(
                      onPressed: _onSaveWithDialogPressed,
                      child: const Text('Save with dialog'),
                    ),
                  ],
                ],
              );
            },
          ),
        ),
      ),
    );
  }

  /// Create example file in temporary directory to work with
  void _createTempPressed() async {
    final folder = await getTemporaryDirectory();
    final filePath = '${folder.path}/$_tempFileName';
    final file = File(filePath);
    final raf = await file.open(mode: FileMode.writeOnlyAppend);
    await raf.writeString('string\n');
    await raf.close();

    log('Created temp file: ${file.path}');
    setState(() {});
  }

  /// Check permission and request it if needed
  void _onCheckPermissionPressed() async {
    final granted = await CRFileSaver.requestWriteExternalStoragePermission();

    log('requestWriteExternalStoragePermission: $granted');
  }

  /// Save created file from temporary directory to downloads folder on Android
  /// or to Documents on IOS
  void _onSaveFilePressed() async {
    final folder = await getTemporaryDirectory();
    final filePath = '${folder.path}/$_tempFileName';
    try {
      final file = await CRFileSaver.saveFile(
        filePath,
        destinationFileName: _testFileName,
      );
      log('Saved to $file');
    } on PlatformException catch (e) {
      log('file saving error: ${e.code}');
    }
  }

  /// Save created file from temporary directory to downloads folder on Android
  /// or to Documents on IOS with native dialog
  void _onSaveWithDialogPressed() async {
    final folder = await getTemporaryDirectory();
    final filePath = '${folder.path}/$_tempFileName';
    String? file;

    try {
      file = await CRFileSaver.saveFileWithDialog(SaveFileDialogParams(
        sourceFilePath: filePath,
        destinationFileName: _testWithDialogFileName,
      ));
      log('Saved to $file');
    } catch (error) {
      log('Error: $error');
    }
  }

  Future<bool> _checkIsTempFileExists() async {
    final folder = await getTemporaryDirectory();
    final filePath = '${folder.path}/$_tempFileName';
    final file = File(filePath);

    return file.exists();
  }
}
