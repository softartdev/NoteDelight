import SwiftUI
import shared

struct ContentView: View {
    @State var notes: [Note]
    
    var body: some View {
        NavigationView {
            MasterView(notes: $notes)
                .navigationBarTitle(Text("Notes"))
                .navigationBarItems(
                    leading: EditButton(),
                    trailing: Button(
                        action: {
                            withAnimation {
                                let newNote = createNote()
                                self.notes.insert(newNote, at: 0)
                            }
                        }
                    ) {
                        Image(systemName: "plus")
                    }
                )
            DetailView()
        }.navigationViewStyle(DoubleColumnNavigationViewStyle())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(notes: notePreviewData)
    }
}
