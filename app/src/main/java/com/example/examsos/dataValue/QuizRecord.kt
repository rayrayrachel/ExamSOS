package com.example.examsos.dataValue

data class QuizRecord(
    val score: Int = 0,
    val difficulty: String = "",
    val totalQuestions: Int = 0,
    val livesRemaining: Int = 0,
    val isWin: Boolean = false,
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
}