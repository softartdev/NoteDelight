//
//  TextField.swift
//  Notes
//
//  Created by Longe, Chris on 1/2/20.
//  Copyright © 2020 Arshad, Fatima. All rights reserved.
//

import SwiftUI

// We will provide this pre-built to the students
struct TextView: UIViewRepresentable {
    @Binding var text: String

    func makeCoordinator() -> Coordinator {
        return(Coordinator(self))
    }
    
    func makeUIView(context: Context) -> UITextView {
        let textView = UITextView()
        textView.delegate = context.coordinator
        textView.font = UIFont.systemFont(ofSize: 16)
        return textView
    }
    
    func updateUIView(_ uiView: UITextView, context: Context) {
        uiView.text = text
    }

    class Coordinator: NSObject, UITextViewDelegate {
        var textView: TextView

        init(_ uiTextView: TextView) {
            self.textView = uiTextView
        }

        func textViewDidChange(_ textView: UITextView) {
            self.textView.text = textView.text
        }
    }
}

struct TextField_Previews: PreviewProvider {
    @State static var textValue = "Note 1\nTesting 1, 2, 3..."
    static var previews: some View {
        TextView(text: $textValue)
    }
}
