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
    private let queryUseCase: QueryUseCase
    @Published var state: DetailViewState = DetailViewState.loading
    
    init(queryUseCase: QueryUseCase) {
        self.queryUseCase = queryUseCase
    }
    
    func loadNote(id: Int64) {
        self.state = DetailViewState.loading
        queryUseCase.loadNote(noteId: id, completionHandler: { note, error in
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
        queryUseCase.saveNote(id: id, title: title, text: text, completionHandler: { note, error in
            if let note = note {
                if case .loaded(_) = self.state {
                    self.state = .saved(note)
                } else {
                    self.state = .error("Illegal state != loaded before save")
                }
            } else {
                print("Unexpected error: \(String(describing: error))")
                self.state = .error(error?.localizedDescription ?? "error")
            }
        })
    }
    
    func stateIsSaved() -> Bool {
        if case .saved(_) = self.state {
            return true
        } else {
            return false
        }
    }
    
    func checkSave() {
        if case .saved(let note) = self.state {
            self.state = .loaded(note)
        } else {
            self.state = .error("Illegal state != saved before check save")
        }
    }
}

enum DetailViewState {
    case loading
    case loaded(Note)
    case saved(Note)
    case error(String?)
}
