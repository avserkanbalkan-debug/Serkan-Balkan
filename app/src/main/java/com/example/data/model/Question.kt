package com.example.data.model

data class Question(
    val q: String,
    val options: List<String>,
    val answer: Int,
    val sol: String,
    val subject: String = ""
)

data class UserQuestionAnswer(
    val question: Question,
    val selectedIndex: Int
)

data class OverallStats(
    val totalQuestions: Int = 0,
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val totalNet: Double = 0.0,
    val accuracyPercent: Int = 0
)

enum class Screen {
    HOME,
    LOADING,
    QUIZ,
    RESULT,
    SETTINGS
}
