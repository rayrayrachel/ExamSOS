package com.example.examsos.api

import com.example.examsos.dataValue.CategoryQuestionCountResponse
import com.example.examsos.dataValue.TriviaCategoryResponse
import com.example.examsos.dataValue.TriviaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null
    ): TriviaResponse

    @GET("api_category.php")
    suspend fun getCategories(): TriviaCategoryResponse

    // New endpoint to get the question count for a specific category
    @GET("api_count.php")
    suspend fun getCategoryQuestionCount(
        @Query("category") category: Int
    ): CategoryQuestionCountResponse
}
