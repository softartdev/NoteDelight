//
//  SplashView.swift
//  iosApp
//
//  Created by Артур on 06.12.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SplashView: View {
    @ObservedObject var viewModel: SplashViewModel
    
    var body: some View {
        splashView()
            .navigationTitle("Note Delight")
            .onAppear(perform: {
                self.viewModel.check()
            })
    }
    
    private func splashView() -> AnyView {
        switch viewModel.state {
            case .loading:
                return AnyView(LoadingView())
            case .result(let dbIsEncrypted):
                if dbIsEncrypted {
                    return AnyView(SignInView(viewModel: SignInViewModel(cryptUseCase: viewModel.cryptUseCase, noteUseCase: viewModel.noteUseCase)))
                } else {
                    return AnyView(ContentView(viewModel: ContentViewModel(noteUseCase: viewModel.noteUseCase)))
                }
            case .error(let description):
                return AnyView(ErrorView(message: description))
        }
    }
}

struct SplashView_Previews: PreviewProvider {
    static let repo = IosDbRepo()
    static let cryptUseCase = CryptUseCase(dbRepo: repo)
    static let noteUseCase = NoteUseCase(dbRepo: repo)
    static let viewModel = SplashViewModel(dbRepo: repo, cryptUseCase: cryptUseCase, noteUseCase: noteUseCase)
    static var previews: some View {
        SplashView(viewModel: self.viewModel)
    }
}
