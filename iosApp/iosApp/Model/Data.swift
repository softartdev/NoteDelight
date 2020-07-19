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
var secondNote = Note(id: 1, title: "second title", text: "second text", dateCreated: Date(), dateModified: Date())
var thirdNote = Note(id: 1, title: "third title", text: "third text", dateCreated: Date(), dateModified: Date())

let noteData: [Note] = [firstNote, secondNote, thirdNote]

