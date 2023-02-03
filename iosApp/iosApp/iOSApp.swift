import SwiftUI
import iosComposeKit

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate

    var body: some Scene {
        WindowGroup {
            Group {
                ZStack {
                    Color(UIColor.systemGray)
                        .ignoresSafeArea()
                    
                    ComposeController(skikoHelper: appDelegate.skikoHelper)
                }
            }
        }
    }
}
