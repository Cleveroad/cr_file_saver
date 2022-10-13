import Foundation

typealias ResultCompletion<T> = (T, FlutterError?) -> Void

class FileSaverImpl: NSObject, FileSaverApi, UIDocumentPickerDelegate {
    private var tempFileUrl: URL?
    private var pendingCompletion: ResultCompletion<String?>? = nil
    
    func saveFileFilePath(_ filePath: String, destinationFileName: String?, completion: @escaping (String?, FlutterError?) -> Void) {
        let sourceFileUrl = URL(fileURLWithPath: filePath).standardized
        let destinationFileNameFromPath = destinationFileName ?? sourceFileUrl.lastPathComponent
                
        // creating temp file that will be moved into destination folder later
        makeTempFile(sourceFileUrl: sourceFileUrl, destinationFileName: destinationFileNameFromPath, completion)
        var destinationPath: URL?
        do {
            var doc = try FileManager.default.url(for: .documentDirectory,
                                                  in: .userDomainMask,
                                                  appropriateFor: nil,
                                                  create: true)
            doc.appendPathComponent(destinationFileNameFromPath)
            destinationPath = doc
            NSLog("Destination folder: \(doc)")
            try FileManager.default.copyItem(at: tempFileUrl!, to: doc)
        } catch {
            completion(nil, FlutterError(code: "CouldNotCopyException", message: error.localizedDescription, details: nil))
        }
        
        deleteTempFile()
        completion(destinationPath?.path, nil)
    }
    
    /// On iOS this method is unnecessary
    func requestWriteExternalStoragePermission(completion: @escaping (NSNumber?, FlutterError?) -> Void) {
        completion(1, nil)
    }
    
    func saveFile(with params: SaveFileDialogParams, completion: @escaping (String?, FlutterError?) -> Void) {
        pendingCompletion = completion
        let sourceFile = params.sourceFilePath
        let sourceFileUrl = URL(fileURLWithPath: sourceFile).standardized
        
        // creating temp file that will be moved into destination folder later
        makeTempFile(sourceFileUrl: sourceFileUrl, destinationFileName: params.destinationFileName, completion)
        
        guard let parentVC = (UIApplication.shared.delegate as! FlutterAppDelegate).window.rootViewController else {
            completion(nil, FlutterError(code: "InvalidRootViewControllerException", message: "Could not retrieve root vc", details: nil))
            return
        }
        
        var documentPickerVC: UIDocumentPickerViewController
        if #available(iOS 14.0, *) {
            documentPickerVC = UIDocumentPickerViewController(forExporting: [tempFileUrl!])
        } else {
            documentPickerVC = UIDocumentPickerViewController(url: tempFileUrl!, in: .exportToService)
        }
        documentPickerVC.delegate = self
        
        parentVC.present(documentPickerVC, animated: true)
    }
    
    private func makeTempFile(sourceFileUrl: URL, destinationFileName: String, _ completion: ResultCompletion<String?>) {
        // creating temporary file
        let tempDirectory = NSTemporaryDirectory()

        do {
            tempFileUrl = NSURL.fileURL(withPathComponents: [tempDirectory, destinationFileName])
            // remove existing temp file
            if FileManager.default.fileExists(atPath: tempFileUrl!.path) {
                try FileManager.default.removeItem(at: tempFileUrl!)
            }
            // copying to temp file
            NSLog("Copying to temp \(tempFileUrl!)")
            try FileManager.default.copyItem(at: sourceFileUrl, to: tempFileUrl!)
        } catch {
            completion(nil, FlutterError(code: "CouldNotCreateTempFileException", message: error.localizedDescription, details: nil))
        }
    }
    
    /// Removing temporary file that might stay after copying (or canceling dialog)
    private func deleteTempFile() {
        if tempFileUrl != nil {
            do {
                NSLog("Removing \(tempFileUrl!) after using")
                if FileManager.default.fileExists(atPath: tempFileUrl!.path) {
                    try FileManager.default.removeItem(at: tempFileUrl!)
                }
                tempFileUrl = nil
            } catch {
                NSLog(error.localizedDescription)
            }
        }
    }
    
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        NSLog("Picker closed")
        deleteTempFile()
        if !urls.isEmpty {
            pendingCompletion?(urls.first!.path, nil)
            pendingCompletion = nil
        }
    }
    
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        NSLog("Picker closed by user")
        deleteTempFile()
        pendingCompletion?(nil, nil)
        pendingCompletion = nil
    }
}
