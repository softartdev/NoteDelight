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
    let noteUseCase: NoteUseCase
    @Published var state: ContentViewState = ContentViewState.loading
    
    init(noteUseCase: NoteUseCase) {
        self.noteUseCase = noteUseCase
    }

    func loadNotes() {
        self.state = ContentViewState.loading
        noteUseCase.launchNotes(onSuccess: { [weak self] data in
            self?.state = ContentViewState.result(data)
        }, onFailure: { [weak self] throwable in
            self?.state = ContentViewState.error(throwable.message)
        })
    }
    
    func createNote() {
        noteUseCase.createNote(title: "", text: "", completionHandler: { note, error in
            if let note = note {
                print("Note saved: \(note)")
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = ContentViewState.error(error?.localizedDescription ?? "error")
            }
        })
    }
    
    func openSettings() {
        self.state = ContentViewState.settings
    }
    
    func deleteNote(noteId: Int64) {
        noteUseCase.deleteNoteUnit(id: noteId, completionHandler: { unit, error in
            if unit != nil {
                print("Delete note with id=\(noteId)")
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = ContentViewState.error(error?.localizedDescription ?? "error")
            }
        })
    }
}

enum ContentViewState {
    case loading
    case result([Note])
    case settings
    case error(String?)
}
