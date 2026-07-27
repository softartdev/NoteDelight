import SwiftUI
import UIKit
import iosComposeKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    private var appDelegate

    var body: some Scene {
        WindowGroup {
            ComposeView(appLauncher: appDelegate.appLauncher)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
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
