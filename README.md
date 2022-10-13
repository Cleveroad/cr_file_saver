# cr_file_saver
![Header image](https://github.com/Cleveroad/cr_file_saver/raw/main/images/cr_file_saver.png)

## Cleveroad introduces file saver for Flutter

### Features:
- Request WriteExternalStoragePermission for Android if needed.
- Save file to downloads directory on Android or Document folder on IOS.
- Save file through standard file saving dialog.

### Future features:
- Saving file to a specific directory.


## Setup
In the `pubspec.yaml` of your flutter project, add the following dependency:
```yaml
dependencies:
  ...
  cr_file_saver: ^0.0.1+1
```

If you are using android 9 and below add this permission to project manifest:

```xml
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

In your dart file add the following import:

```dart
import 'package:cr_file_saver/cr_file_saver.dart';
```

## Usage
```CRFileSaver``` has several static methods to work with:
* ```requestWriteExternalStoragePermission``` to check for permission and ask it if needed.
* ```saveFile``` simply saving file with provided file path and desired file name.
* ```saveFileWithDialog``` save file through standard file saving dialog. Note that this method will throw ```NoResolvedActivityException``` if Android device has 30 api or higher