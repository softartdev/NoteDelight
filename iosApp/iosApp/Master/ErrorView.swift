//
//  ErrorView.swift
//  iosApp
//
//  Created by Артур on 07.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct ErrorView: View {
    var message: String?
    
    var body: some View {
        Text(message ?? "Error")
    }
}

struct ErrorView_Previews: PreviewProvider {
    static var previews: some View {
        ErrorView()
    }
}
