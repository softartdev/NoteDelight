package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Update
import androidx.room3.migration.Migration
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Database(entities = [Note::class], version = 2, exportSchema = false)
@ConstructedBy(NoteDatabaseConstructor::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteRoomDao
}

// SQLDelight is already at version 2 because it contains the empty 1.sqm migration.
internal val NOTE_DATABASE_MIGRATION_1_2 = Migration(1, 2) { }

@Entity(tableName = "note")
@ColumnTypeConverters(NoteTypeConverters::class)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    var dateModified: LocalDateTime
)

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
interface NoteRoomDao {

    @Query("SELECT * FROM note ORDER BY dateModified DESC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT count(*) FROM note")
    suspend fun getCount(): Long

    @Query("SELECT * FROM note ORDER BY dateModified DESC")
    suspend fun getAll(): List<Note>

    @Query("SELECT * FROM note ORDER BY dateModified DESC")
    fun pagingSource(): PagingSource<Int, Note>

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun load(id: Long): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM note")
    suspend fun deleteAll()
}
