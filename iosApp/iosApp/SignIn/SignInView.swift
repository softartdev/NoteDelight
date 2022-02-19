//
//  SignInView.swift
//  iosApp
//
//  Created by Артур on 01.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SignInView: View {
    @ObservedObject private(set) var viewModel: SignInViewModel
    
    var body: some View {
        signInView()
    }
    
    private func signInView() -> AnyView {
        switch viewModel.state {
            case .form:
                return AnyView(FormView(viewModel: self.viewModel))
            case .loading:
                return AnyView(LoadingView())
            case .success:
                return AnyView(ContentView(viewModel: ContentViewModel(noteUseCase: viewModel.noteUseCase)))
        }
    }
    
    struct FormView: View {
        @ObservedObject var viewModel: SignInViewModel
        
        var appName: String = MR.strings().app_name.desc().localized()

        var body: some View {
            NavigationView {
                Form {
                    Section(
                        header: Text("Password"),
                        footer: Text(viewModel.message)
                            .foregroundColor(.red)
                    ) {
                        SecureField("Type Password", text: $viewModel.password) {
                            print("typed: \(viewModel.password)")
                            viewModel.checkPassword()
                        }
                    }
                    Button("Sign In", action: {
                        print("tap: \(viewModel.password)")
                        viewModel.checkPassword()
                    }).frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
                }.navigationBarTitle(appName)
            }
        }
    }
}

struct SignInView_Previews: PreviewProvider {
    static let repo = IosDbRepo()
    static let cryptUseCase = CryptUseCase(dbRepo: repo)
    static let noteUseCase = NoteUseCase(dbRepo: repo)
    static var previews: some View {
        SignInView(viewModel: SignInViewModel(cryptUseCase: cryptUseCase, noteUseCase: noteUseCase))
    }
}
