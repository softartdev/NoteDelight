//
//  NoteList.swift
//  iosApp
//
//  Created by Артур on 20.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MasterView: View {
    @Binding var notes: [Note]
    @ObservedObject private(set) var viewModel: ContentViewModel
    
    var body: some View {
        List {
            ForEach(notes, id: \.id) { note in
                NavigationLink(destination: DetailView(noteId: note.id, viewModel: DetailViewModel(queryUseCase: self.viewModel.queryUseCase))) {
                    NoteRow(note: note)
                }
            }.onDelete { indices in
                indices.forEach { (index: Int) in
                    self.viewModel.deleteNote(noteId: self.notes[index].id)
                }
            }
        }
    }
}

struct NoteList_Previews: PreviewProvider {
    static var previews: some View {
        MasterView(notes: .constant(notePreviewData), viewModel: ContentViewModel(queryUseCase: QueryUseCase(noteQueries: IosDbRepo().noteQueries)))
    }
}
