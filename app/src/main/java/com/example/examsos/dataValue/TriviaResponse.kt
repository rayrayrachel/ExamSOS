package com.example.examsos.dataValue

data class TriviaResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)
