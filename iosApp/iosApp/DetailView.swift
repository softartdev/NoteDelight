//
//  NoteDetail.swift
//  iosApp
//
//  Created by Артур on 15.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DetailView: View {
    var note: Note?
    
    var body: some View {
        Group {
            if note != nil {
                TextView(text: .constant(note!.text))
            } else {
                Text("Click + for create new note")
            }
        }.navigationBarTitle(note?.title ?? "Note")
    }
}

struct NoteDetail_Previews: PreviewProvider {
    static let prevNote: Note = createNote()
    
    static var previews: some View {
        Group {
            NavigationView {
                DetailView(note: nil)
            }
            NavigationView {
                DetailView(note: prevNote)
            }
        }.previewLayout(.fixed(width: 500, height: 500))
    }
}
