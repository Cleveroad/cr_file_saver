#import "CrFileSaverPlugin.h"
#if __has_include(<cr_file_saver/cr_file_saver-Swift.h>)
#import <cr_file_saver/cr_file_saver-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "cr_file_saver-Swift.h"
#endif

@implementation CrFileSaverPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftCrFileSaverPlugin registerWithRegistrar:registrar];
}
@end
