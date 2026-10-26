import Social
import UIKit
import UniformTypeIdentifiers

@objc(ShareViewController)
final class ShareViewController: SLComposeServiceViewController {
    private static let appGroupId = "group.com.softartdev.notedelight"
    private static let sharedTextKey = "shared_text"

    override func presentationAnimationDidFinish() {
        super.presentationAnimationDidFinish()
        guard contentText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return }
        loadTextFileIfNeeded()
    }

    override func isContentValid() -> Bool {
        !contentText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    override func didSelectPost() {
        let trimmed = contentText.trimmingCharacters(in: .whitespacesAndNewlines)
        if !trimmed.isEmpty {
            UserDefaults(suiteName: Self.appGroupId)?.set(trimmed, forKey: Self.sharedTextKey)
        }
        extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        []
    }

    private func loadTextFileIfNeeded() {
        guard let items = extensionContext?.inputItems as? [NSExtensionItem] else { return }
        for item in items {
            for provider in item.attachments ?? [] where provider.hasItemConformingToTypeIdentifier(UTType.plainText.identifier) {
                provider.loadItem(forTypeIdentifier: UTType.plainText.identifier, options: nil) { [weak self] data, _ in
                    guard let url = data as? URL, url.isFileURL else { return }
                    let accessed = url.startAccessingSecurityScopedResource()
                    defer {
                        if accessed {
                            url.stopAccessingSecurityScopedResource()
                        }
                    }
                    guard let text = try? String(contentsOf: url, encoding: .utf8) else { return }
                    DispatchQueue.main.async {
                        self?.textView.text = text
                        self?.validateContent()
                    }
                }
                return
            }
        }
    }
}
