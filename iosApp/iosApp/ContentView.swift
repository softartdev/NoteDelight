import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        Text(Platform_common_mainKt.createMultiplatformMessage())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
