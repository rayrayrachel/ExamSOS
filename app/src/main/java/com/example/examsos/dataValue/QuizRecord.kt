package com.example.examsos.dataValue

data class QuizRecord(
    val score: Int,
    val difficulty: String,
    val totalQuestions: Int,
    val livesRemaining: Int,
    val isWin: Boolean,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)