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
    
    var body: some View {
        Group {
            if noteId != nil {
                textView(noteId: noteId!)
                    .onAppear(perform: {
                        self.viewModel.loadNote(id: self.noteId!)
                    })
                    .navigationBarTitle("",displayMode: .inline)
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
                return AnyView(EditView(noteId: self.noteId, viewModel: self.viewModel, title: note.title, text: note.text))
            case .error(let description):
                return AnyView(ErrorView(message: description))
        }
    }

    struct EditView: View {
        var noteId: Int64?
        var viewModel: DetailViewModel
        @State var title: String
        @State var text: String
        @State var isSaveAlertShowing = false
        
        var body: some View {
            VStack {
                TextField("Title", text: $title)
                    .font(.title)
                    .padding(EdgeInsets(top: 10, leading: 10, bottom: 0, trailing: 10))
                Spacer()
                TextView(text: $text)
            }.navigationBarItems(trailing: Button(action: {
                    self.isSaveAlertShowing.toggle()
                }, label: {
                    Text("Save")
                }))
            .alert(isPresented: $isSaveAlertShowing) {
                Alert(title: Text("Save"), message: Text("Are you sure you want to save this?"), primaryButton: .destructive(Text("OK")) {
                    self.viewModel.saveNote(id: self.noteId!, title: self.title, text: self.text)
                }, secondaryButton: .cancel())
            }
        }
    }
}

struct NoteDetail_Previews: PreviewProvider {
    static let prevNote: Note = createNote()
    static let detailViewModel = DetailViewModel(noteUseCase: NoteUseCase(dbRepo: IosDbRepo()))
    
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
