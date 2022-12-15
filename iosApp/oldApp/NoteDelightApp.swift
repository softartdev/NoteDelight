//
//  NoteDelightApp.swift
//  iosApp
//
//  Created by Артур on 06.12.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

@main
struct NoteDelightApp: App {
    
    var body: some Scene {
        
        let iosDbRepo = IosDbRepo()
        let cryptUseCase = CryptUseCase(dbRepo: iosDbRepo)
        let noteUseCase = NoteUseCase(dbRepo: iosDbRepo)
        
        let viewModel = SplashViewModel(dbRepo: iosDbRepo, cryptUseCase: cryptUseCase, noteUseCase: noteUseCase)
        
        WindowGroup {
            SplashView(viewModel: viewModel)
        }
    }
}
