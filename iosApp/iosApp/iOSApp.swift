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
                .onChange(of: scenePhase, perform: { newPhase in
                    switch newPhase {
                        case .background: appHelper.stopLifecycle()
                        case .inactive: appHelper.pauseLifecycle()
                        case .active: appHelper.resumeLifecycle()
                        @unknown default: break
                    }
                })
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}
