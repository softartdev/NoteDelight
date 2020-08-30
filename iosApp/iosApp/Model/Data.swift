//
//  Data.swift
//  iosApp
//
//  Created by Артур on 20.07.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import Foundation
import shared

var firstNote = Note(id: 1, title: "first title", text: "first text", dateCreated: Date(), dateModified: Date())
var secondNote = Note(id: 2, title: "second title", text: "second text", dateCreated: Date(), dateModified: Date())
var thirdNote = Note(id: 3, title: "third title", text: "third text", dateCreated: Date(), dateModified: Date())

var notePreviewData: [Note] = [firstNote, secondNote, thirdNote]

func createNote() -> Note {
    let lastId: Int64 = notePreviewData.last?.id ?? 0
    let newId: Int64 = lastId + 1
    let newNote = Note(id: newId, title: "New note title", text: "New text", dateCreated: Date(), dateModified: Date())
    notePreviewData.append(newNote)
    return newNote
}

