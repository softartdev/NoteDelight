//
//  RootHolder.swift
//  iosApp
//
//  Created by Artur Babichev on 18.08.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import iosComposeKit

class RootHolder : ObservableObject {
    let appHelper: AppHelper
    
    init() {
        appHelper = AppHelper()
        appHelper.appInit()
    }
}
