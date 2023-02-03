//
//  AppDelegate.swift
//  iosApp
//
//  Created by Artur Babichev on 05.12.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import UIKit
import iosComposeKit

class AppDelegate: NSObject, UIApplicationDelegate {
    
    let skikoHelper = SkikoHelper()
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        skikoHelper.appInit()
        return true
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        skikoHelper.resumeLifecycle()
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        skikoHelper.stopLifecycle()
    }

    func applicationWillTerminate(_ application: UIApplication) {
        skikoHelper.destroyLifecycle()
    }
}
