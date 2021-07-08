import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ContentViewModel
    @State private var showingAlert = false
    
    var body: some View {
        NavigationView {
            listView()
                .navigationBarTitle(Text("Notes"))
                .navigationBarItems(
                    leading: EditButton(),
                    trailing: HStack {
                        Button(
                            action: {
                                withAnimation {
                                    self.viewModel.createNote()
                                }
                            }
                        ) {
                            Image(systemName: "plus")
                        }
                        Spacer(minLength: 20)
                        Button(action: {
                            showingAlert = true
                        }, label: {
                            Image(systemName: "lock")
                        })
                    })
                .alert(isPresented: $showingAlert, content: {
                    Alert(title: Text("title text"), message: Text("title msg"), primaryButton: .cancel(), secondaryButton: .destructive(Text("Destruct")))
                })
            DetailView(noteId: nil, viewModel: DetailViewModel(noteUseCase: self.viewModel.noteUseCase))
        }.navigationViewStyle(DoubleColumnNavigationViewStyle())
        .onAppear(perform: {
            self.viewModel.loadNotes()
        })
    }
    
    private func listView() -> AnyView {
        switch viewModel.state {
            case .loading:
                return AnyView(LoadingView())
            case .result(let notes):
                return AnyView(MasterView(notes: .constant(notes), viewModel: self.viewModel))
            case .error(let description):
                return AnyView(ErrorView(message: description))
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(viewModel: ContentViewModel(noteUseCase: NoteUseCase(dbRepo: IosDbRepo())))
    }
}
