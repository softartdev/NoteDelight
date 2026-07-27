import FirebaseCore
import UIKit
import iosComposeKit

class AppDelegate: NSObject, UIApplicationDelegate {
    let appLauncher = IosAppLauncher()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        #if DEBUG
        appLauncher.doInit(debug: true)
        #else
        appLauncher.doInit(debug: false)
        #endif
        return true
    }
}
