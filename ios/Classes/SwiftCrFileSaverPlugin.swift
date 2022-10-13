import Flutter
import UIKit

public class SwiftCrFileSaverPlugin: NSObject, FlutterPlugin {
    var fileSaverDelegate: FileSaverApi? = nil
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "cr_file_saver", binaryMessenger: registrar.messenger())
        let fileSaver = FileSaverImpl()
        let instance = SwiftCrFileSaverPlugin(fileSaver)
        registrar.addMethodCallDelegate(instance, channel: channel)
        FileSaverApiSetup(registrar.messenger(), fileSaver)
    }
    
    init(_ saver: FileSaverApi) {
        fileSaverDelegate = saver
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    }
}
