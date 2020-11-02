//
//  DetailViewModel.swift
//  iosApp
//
//  Created by Артур on 07.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import shared

class DetailViewModel: ObservableObject {
    private let noteUseCase: NoteUseCase
    @Published var state: DetailViewState = DetailViewState.loading
    
    init(noteUseCase: NoteUseCase) {
        self.noteUseCase = noteUseCase
    }
    
    func loadNote(id: Int64) {
        self.state = DetailViewState.loading
        noteUseCase.loadNote(noteId: id, completionHandler: { note, error in
            if let note = note {
                self.state = .loaded(note)
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = .error(error?.localizedDescription ?? "error")
            }
        })
    }
    
    func saveNote(id: Int64, title: String, text: String) {
        print("Save note id=\(id),title=\(title),text=\(text)")
        noteUseCase.saveNote(id: id, title: title, text: text, completionHandler: { note, error in
            if let note = note {
                print("Note saved: \(note)")
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = .error(error?.localizedDescription ?? "error")
            }
        })
    }
}

enum DetailViewState {
    case loading
    case loaded(Note)
    case error(String?)
}
