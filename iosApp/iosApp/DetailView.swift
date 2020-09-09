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
    var noteId: Int64?
    @ObservedObject private(set) var viewModel: DetailViewModel
    @State var isSaveAlertShowing = false
    
    var body: some View {
        Group {
            if noteId != nil {
                textView(noteId: noteId!)
                .onAppear(perform: {
                    self.viewModel.loadNote(id: self.noteId!)
                })
            } else {
                Text("Click + for create new note")
                    .navigationBarTitle("Note")
            }
        }
    }
    
    private func textView(noteId: Int64) -> AnyView {
        switch viewModel.state {
            case .loading:
                return AnyView(LoadingView())
            case .loaded(let note):
                return textView(note: note)
            case .error(let description):
                return AnyView(ErrorView(message: description))
        }
    }
    
    private func textView(note: Note) -> AnyView {
        let textView = TextView(text: note.text)
        return AnyView(textView
            .navigationBarTitle(note.title)
            .navigationBarItems(trailing: Button(action: {
                self.isSaveAlertShowing.toggle()
            }, label: {
                Text("Save")
            }))
            .alert(isPresented: $isSaveAlertShowing) {
                Alert(title: Text("Save"), message: Text("Are you sure you want to save this?"), primaryButton: .destructive(Text("OK")) {
                    self.viewModel.saveNote(id: self.noteId!, title: note.title, text: textView.text)
                }, secondaryButton: .cancel())
        })
    }
}

struct NoteDetail_Previews: PreviewProvider {
    static let prevNote: Note = createNote()
    static let detailViewModel = DetailViewModel(queryUseCase: QueryUseCase(noteQueries: IosDbRepo().noteQueries))
    
    static var previews: some View {
        Group {
            NavigationView {
                DetailView(noteId: nil, viewModel: detailViewModel)
            }
            NavigationView {
                DetailView(noteId: prevNote.id, viewModel: detailViewModel)
            }
        }.previewLayout(.fixed(width: 500, height: 500))
    }
}
