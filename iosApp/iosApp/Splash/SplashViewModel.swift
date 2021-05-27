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
    let cryptUseCase: CryptUseCase
    @Published var state: SplashViewState = SplashViewState.loading
    
    init(cryptUseCase: CryptUseCase) {
        self.cryptUseCase = cryptUseCase
    }
    
    func check(dbRepo: DatabaseRepo) {
        dbRepo.buildDatabaseInstanceIfNeed(passphrase: "")
        self.state = SplashViewState.loading
        self.cryptUseCase.isDbEncrypted(completionHandler: { encrypted, error in
            if let isEncrypted = encrypted {
                self.state = SplashViewState.result(isEncrypted as! Bool)
            } else {
                self.state = SplashViewState.error("Error: \(String(describing: error?.localizedDescription)).")
            }
        })
    }
    
}

enum SplashViewState {
    case loading
    case result(Bool)
    case error(String?)
}
