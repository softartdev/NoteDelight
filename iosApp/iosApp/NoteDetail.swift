//
//  NoteDetail.swift
//  iosApp
//
//  Created by Артур on 15.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteDetail: View {
    @Binding var note: Note
    
    var body: some View {
        TextView(text: .constant(note.text))
                .navigationBarTitle(note.title)
    }
}

struct NoteDetail_Previews: PreviewProvider {
    static let prevNote: Note = createNote()
    static let bindNote: Binding<Note> = .constant(prevNote)
    
    static var previews: some View {
        NavigationView {
            NoteDetail(note: bindNote)
        }
    }
}
