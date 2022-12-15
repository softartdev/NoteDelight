//
//  SplashViewModel.swift
//  iosApp
//
//  Created by Артур on 06.12.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import shared

class SplashViewModel: ObservableObject {
    let dbRepo: DatabaseRepo
    let cryptUseCase: CryptUseCase
    let noteUseCase: NoteUseCase
    @Published var state: SplashViewState = SplashViewState.loading
    
    init(dbRepo: DatabaseRepo, cryptUseCase: CryptUseCase, noteUseCase: NoteUseCase) {
        self.dbRepo = dbRepo
        self.cryptUseCase = cryptUseCase
        self.noteUseCase = noteUseCase
    }
    
    func check() {
        dbRepo.buildDatabaseInstanceIfNeed(passphrase: "")
        self.state = SplashViewState.loading
        self.cryptUseCase.isDbEncrypted(completionHandler: { encrypted, error in
            if let isEncrypted = encrypted {
                self.state = SplashViewState.result(isEncrypted as! Bool)
            } else {
                self.state = SplashViewState.error("Error: \(String(describing: error?.localizedDescription)).")
            }
//            self.state = SplashViewState.result(true)//TODO:revert before merge!
        })
    }
    
}

enum SplashViewState {
    case loading
    case result(Bool)
    case error(String?)
}
