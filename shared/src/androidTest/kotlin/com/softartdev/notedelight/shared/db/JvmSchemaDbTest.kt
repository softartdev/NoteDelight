package com.softartdev.notedelight.shared.db

import kotlin.test.Test
import kotlin.test.assertTrue

class JvmSchemaDbTest : BaseDbTest() {

  @Test
  fun someData() {
    assertTrue(Db.instance.noteQueries.getAll().executeAsList().isNotEmpty())
  }

  @Test
  fun notesCreated() {
    val teams = getDb().noteQueries.getAll().executeAsList()
    assertTrue(teams.any {
      it.title == TestSchema.secondNote.title
    })
  }
}
