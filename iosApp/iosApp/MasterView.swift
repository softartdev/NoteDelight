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
    
    var body: some View {
        List {
            ForEach(notes, id: \.id) { note in
                NavigationLink(destination: DetailView(note: note)) {
                    NoteRow(note: note)
                }
            }.onDelete { indices in
                indices.forEach { self.notes.remove(at: $0) }
            }
        }
    }
}

struct NoteList_Previews: PreviewProvider {
    static var previews: some View {
        MasterView(notes: .constant(notePreviewData))
    }
}
