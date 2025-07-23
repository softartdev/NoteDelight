import SwiftUI
import iosComposeKit

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate
    
    @Environment(\.scenePhase)
    var scenePhase: ScenePhase

    var appHelper: AppHelper { appDelegate.rootHolder.appHelper }

    var body: some Scene {
        WindowGroup {
            ComposeController(appHelper: appHelper)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}
