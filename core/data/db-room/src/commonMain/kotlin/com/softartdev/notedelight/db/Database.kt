package com.softartdev.notedelight.db

import androidx.paging.PagingSource
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Database(entities = [Note::class], version = 1, exportSchema = false)
@ConstructedBy(NoteDatabaseConstructor::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteRoomDao
}

@Entity
@TypeConverters(NoteTypeConverters::class)
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val text: String,
    val dateCreated: LocalDateTime,
    var dateModified: LocalDateTime
)

@Dao
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