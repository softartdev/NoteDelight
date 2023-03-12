import SwiftUI
import iosComposeKit

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate

    var body: some Scene {
        WindowGroup {
            ComposeController(skikoHelper: appDelegate.skikoHelper)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}
