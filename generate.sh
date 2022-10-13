flutter pub run pigeon \
  --input pigeons/messages.dart \
  --dart_out lib/src/pigeon.dart \
  --objc_header_out ios/Classes/pigeon.h \
  --objc_source_out ios/Classes/pigeon.m \
  --java_out ./android/src/main/kotlin/com/cleveroad/cr_file_saver/Pigeon.java \
  --java_package "com.cleveroad.cr_file_saver"