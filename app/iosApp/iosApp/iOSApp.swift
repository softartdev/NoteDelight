import SwiftUI
import UIKit
import iosComposeKit

@main
struct iOSApp: App {
    private static let appGroupId = "group.com.softartdev.notedelight"
    private static let sharedTextKey = "shared_text"

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    private var appDelegate

    @Environment(\.scenePhase)
    private var scenePhase

    var body: some Scene {
        WindowGroup {
            ComposeView(appLauncher: appDelegate.appLauncher)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
                .onOpenURL(perform: handleIncomingURL)
                .onChange(of: scenePhase) { newPhase in
                    if newPhase == .active {
                        consumeSharedUserDefaults()
                    }
                }
        }
    }

    private func handleIncomingURL(_ url: URL) {
        guard url.isFileURL else { return }
        let accessed = url.startAccessingSecurityScopedResource()
        defer {
            if accessed {
                url.stopAccessingSecurityScopedResource()
            }
        }
        if let text = try? String(contentsOf: url, encoding: .utf8), !text.isEmpty {
            appDelegate.appLauncher.onSharedText(text: text)
        }
    }

    private func consumeSharedUserDefaults() {
        guard let defaults = UserDefaults(suiteName: Self.appGroupId),
              let text = defaults.string(forKey: Self.sharedTextKey),
              !text.isEmpty else {
            return
        }
        defaults.removeObject(forKey: Self.sharedTextKey)
        appDelegate.appLauncher.onSharedText(text: text)
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    let appLauncher: IosAppLauncher

    func makeUIViewController(context: Context) -> UIViewController {
        appLauncher.mainViewController
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
