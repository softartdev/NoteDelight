//
//  NoteList.swift
//  iosApp
//
//  Created by Артур on 20.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct NoteList: View {
    var body: some View {
        NavigationView {
            List(noteData, id: \.id) { note in
                NavigationLink(destination: NoteDetail(note: note)) {
                    NoteRow(note: note)
                }
            }
            .navigationBarTitle(Text("Notes"))
        }
    }
}

struct NoteList_Previews: PreviewProvider {
    static var previews: some View {
        NoteList()
    }
}
