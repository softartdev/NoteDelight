//
//  NoteList.swift
//  iosApp
//
//  Created by Артур on 20.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteList: View {
    @State var notes: [Note]
    
    var body: some View {
        NavigationView {
            List(notes, id: \.id) { note in
                NavigationLink(destination: NoteDetail(note: note)) {
                    NoteRow(note: note)
                }
            }
            .navigationBarTitle(Text("Notes"))
            .navigationBarItems(trailing:
                Button(action: {
                    let newNote = createNote()
                    self.notes.insert(newNote, at: 0)
                }) {
                    Image(systemName: "plus")
                }
            )
        }
    }
}

struct NoteList_Previews: PreviewProvider {
    static var previews: some View {
        NoteList(notes: notePreviewData)
    }
}
