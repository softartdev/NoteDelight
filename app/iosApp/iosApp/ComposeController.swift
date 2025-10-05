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
    
    let appHelper: AppHelper
    
    func makeUIViewController(context: Context) -> some UIViewController {
        appHelper.appUIViewController
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
    }
}
