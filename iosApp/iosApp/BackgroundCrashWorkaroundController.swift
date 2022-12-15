//
//  BackgroundCrashWorkaroundController.swift
//  iosApp
//
//  Created by Artur Babichev on 05.12.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import ios_compose_kit

class BackgroundCrashWorkaroundController: UIViewController {
    
    let composeController: UIViewController
    
    init(skikoHelper: SkikoHelper) {
        composeController = skikoHelper.applicationUIViewController
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if composeController.parent == nil {
            addChild(composeController)
            composeController.view.frame = view.bounds
            view.addSubview(composeController.view)
            composeController.didMove(toParent: self)
        }
    }
}
