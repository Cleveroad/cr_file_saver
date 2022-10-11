import 'package:pigeon/pigeon.dart';

@HostApi()
abstract class FileSaverApi {
  @async
  String? saveFile(
    String filePath,
    // TODO: Implement saving into custom directory (pictures, DCIM, documents etc.)
    // DestinationDirectory directory,
    String? destinationFileName,
  );

  @async
  bool? requestWriteExternalStoragePermission();

  @async
  String? saveFileWithDialog(SaveFileDialogParams params);
}

class SaveFileDialogParams {
  final String sourceFilePath;
  final Uint8List? data;
  final String destinationFileName;

  SaveFileDialogParams({
    required this.sourceFilePath,
    required this.destinationFileName,
    this.data,
  });
}

enum DestinationDirectory {
  download,
  images,
  dcim,
  documents,
  music,
  movies,
  podcasts,
}
