//
//  SignInViewModel.swift
//  iosApp
//
//  Created by Артур on 07.07.2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class SignInViewModel: ObservableObject {
    let cryptUseCase: CryptUseCase
    let noteUseCase: NoteUseCase
    
    @Published public var password: String = ""
    @Published public var message: String = ""
    @Published var state: SignInViewState = SignInViewState.form
    
    init(cryptUseCase: CryptUseCase, noteUseCase: NoteUseCase) {
        self.cryptUseCase = cryptUseCase
        self.noteUseCase = noteUseCase
    }
    
    func checkPassword() {
        self.state = SignInViewState.loading
        if (self.password == "1") {
            self.state = SignInViewState.success
        } else {
            self.message = "Incorrect password"
            self.state = SignInViewState.form
        }/*
        cryptUseCase.checkPassword(pass: password, completionHandler: { isCorrect, error in
            if (isCorrect == true) {
                self.state = SignInViewState.success
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = SignInViewState.error(error?.localizedDescription ?? "error")
            }
        })*/
    }
}

enum SignInViewState {
    case form
    case loading
    case success
}
