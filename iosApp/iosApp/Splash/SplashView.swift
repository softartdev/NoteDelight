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
    let dbRepo: DatabaseRepo
    let cryptUseCase: CryptUseCase
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
                    return AnyView(SignInView())
                } else {
                    return AnyView(ContentView(viewModel: ContentViewModel(noteUseCase: NoteUseCase(dbRepo: self.dbRepo))))
                }
            case .error(let description):
                return AnyView(ErrorView(message: description))
        }
    }
}

struct SplashView_Previews: PreviewProvider {
    static let repo = IosDbRepo()
    static let useCae = CryptUseCase(dbRepo: repo)
    static let viewModel = SplashViewModel(cryptUseCase: useCae)
    static var previews: some View {
        SplashView(dbRepo: self.repo, cryptUseCase: self.useCae, viewModel: self.viewModel)
    }
}
