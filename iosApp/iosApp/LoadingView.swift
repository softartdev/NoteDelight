//
//  ProgressView.swift
//  iosApp
//
//  Created by Артур on 06.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct LoadingView: View {
    var body: some View {
        VStack {
            Text("Loading…")
            ActivityIndicator(isAnimating: .constant(true), style: .large)
        }
    }
}

struct ActivityIndicator: UIViewRepresentable {
    @Binding var isAnimating: Bool
    let style: UIActivityIndicatorView.Style

    func makeUIView(context: UIViewRepresentableContext<ActivityIndicator>) -> UIActivityIndicatorView {
        return UIActivityIndicatorView(style: style)
    }

    func updateUIView(_ uiView: UIActivityIndicatorView, context: UIViewRepresentableContext<ActivityIndicator>) {
        isAnimating ? uiView.startAnimating() : uiView.stopAnimating()
    }
}

struct ProgressView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView()
    }
}
