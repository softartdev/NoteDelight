//
//  ComposeController.swift
//  iosApp
//
//  Created by Artur Babichev on 05.12.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import iosComposeKit

struct ComposeController: UIViewControllerRepresentable {
    
    let skikoHelper: SkikoHelper
    
    func makeUIViewController(context: Context) -> some UIViewController {
        BackgroundCrashWorkaroundController(skikoHelper: skikoHelper)
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        uiViewController.view.setNeedsLayout()
    }
}
