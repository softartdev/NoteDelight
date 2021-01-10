//
//  SignInView.swift
//  iosApp
//
//  Created by Артур on 01.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct SignInView: View {
    
    @State var password: String = ""
    
    var body: some View {
        NavigationView {
            Form {
                Section(
                    header: Text("Password"),
                    footer: Text("Error")
                        .foregroundColor(.red)
                ) {
                    SecureField("Type Password", text: $password) {
                        print("Password: \(self.password)")
                    }
                }
                Button("Sign In", action: {
                    print("tap")
                }).frame(minWidth: 0, maxWidth: .infinity, alignment: .center)
            }.navigationBarTitle("Note Delight")
        }
    }
}

struct SignInView_Previews: PreviewProvider {
    static var previews: some View {
        SignInView()
    }
}
