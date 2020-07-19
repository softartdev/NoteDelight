//
//  NoteRow.swift
//  iosApp
//
//  Created by Артур on 15.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteRow: View {
    var note: Note
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(note.title)
                .font(.headline)
                .fontWeight(.medium)
                .multilineTextAlignment(.leading)
            Text(note.dateModified.nsDate.description)
                .font(.footnote)
                .fontWeight(.light)
                .multilineTextAlignment(.trailing)
        }
    }
}

struct NoteRow_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            NoteRow(note: noteData[0])
            NoteRow(note: noteData[1])
            NoteRow(note: noteData[2])
        }
        .previewLayout(.fixed(width: 300, height: 70))
    }
}
