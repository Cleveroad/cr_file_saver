// Autogenerated from Pigeon (v3.2.9), do not edit directly.
// See also: https://pub.dev/packages/pigeon
#import <Foundation/Foundation.h>
@protocol FlutterBinaryMessenger;
@protocol FlutterMessageCodec;
@class FlutterError;
@class FlutterStandardTypedData;

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, DestinationDirectory) {
  DestinationDirectoryDownload = 0,
  DestinationDirectoryImages = 1,
  DestinationDirectoryDcim = 2,
  DestinationDirectoryDocuments = 3,
  DestinationDirectoryMusic = 4,
  DestinationDirectoryMovies = 5,
  DestinationDirectoryPodcasts = 6,
};

@class SaveFileDialogParams;

@interface SaveFileDialogParams : NSObject
/// `init` unavailable to enforce nonnull fields, see the `make` class method.
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)makeWithSourceFilePath:(NSString *)sourceFilePath
    data:(nullable FlutterStandardTypedData *)data
    destinationFileName:(NSString *)destinationFileName;
@property(nonatomic, copy) NSString * sourceFilePath;
@property(nonatomic, strong, nullable) FlutterStandardTypedData * data;
@property(nonatomic, copy) NSString * destinationFileName;
@end

/// The codec used by FileSaverApi.
NSObject<FlutterMessageCodec> *FileSaverApiGetCodec(void);

@protocol FileSaverApi
- (void)saveFileFilePath:(NSString *)filePath destinationFileName:(nullable NSString *)destinationFileName completion:(void(^)(NSString *_Nullable, FlutterError *_Nullable))completion;
- (void)requestWriteExternalStoragePermissionWithCompletion:(void(^)(NSNumber *_Nullable, FlutterError *_Nullable))completion;
- (void)saveFileWithDialogParams:(SaveFileDialogParams *)params completion:(void(^)(NSString *_Nullable, FlutterError *_Nullable))completion;
@end

extern void FileSaverApiSetup(id<FlutterBinaryMessenger> binaryMessenger, NSObject<FileSaverApi> *_Nullable api);

NS_ASSUME_NONNULL_END