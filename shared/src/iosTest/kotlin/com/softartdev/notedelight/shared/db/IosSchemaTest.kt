package com.softartdev.notedelight.shared.db

import kotlin.test.Test
import kotlin.test.assertTrue

class IosSchemaTest : BaseTest() {

  @Test
  fun someData() {
    assertTrue(NoteData.notes().isNotEmpty())
  }

  @Test
  fun notesCreated() {
    val notes = getDb().noteQueries.getAll().executeAsList()
    assertTrue(notes.any {
      it.title == TestSchema.secondNote.title
    })
  }

}
