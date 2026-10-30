package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizHistoryDao {
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: QuizHistoryEntity): Long

    @Query("DELETE FROM quiz_history")
    suspend fun clearAll()

    @Query("DELETE FROM quiz_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
