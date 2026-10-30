package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val total: Int,
    val correct: Int,
    val wrong: Int,
    val net: Double,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)
