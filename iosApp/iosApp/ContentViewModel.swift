//
//  ContentViewModel.swift
//  iosApp
//
//  Created by Артур on 07.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import shared

class ContentViewModel: ObservableObject {
    let queryUseCase: QueryUseCase
    @Published var state: ContentViewState = ContentViewState.loading
    
    init(queryUseCase: QueryUseCase) {
        self.queryUseCase = queryUseCase
    }

    func loadNotes() {
        self.state = ContentViewState.loading
        queryUseCase.launchNotes(onSuccess: { [weak self] data in
            self?.state = ContentViewState.result(data)
        }, onFailure: { [weak self] throwable in
            self?.state = ContentViewState.error(throwable.message)
        })
    }
    
    func createNote() {
        do {
            try self.queryUseCase.addNote(title: "New note title", text: "New note text")
        } catch {
            print("Unexpected error: \(error)")
            self.state = ContentViewState.error(error.localizedDescription)
        }
    }
    
    func deleteNote(noteId: Int64) {
        queryUseCase.deleteNote(id: noteId)
    }
}

enum ContentViewState {
    case loading
    case result([Note])
    case error(String?)
}
