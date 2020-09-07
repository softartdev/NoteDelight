import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ContentViewModel
    
    var body: some View {
        NavigationView {
            listView()
                .navigationBarTitle(Text("Notes"))
                .navigationBarItems(
                    leading: EditButton(),
                    trailing: Button(
                        action: {
                            withAnimation {
                                self.viewModel.createNote()
                            }
                        }
                    ) {
                        Image(systemName: "plus")
                    })
            DetailView()
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
        ContentView(viewModel: ContentViewModel(queryUseCase: QueryUseCase(noteQueries: IosDbRepo().noteQueries)))
    }
}
