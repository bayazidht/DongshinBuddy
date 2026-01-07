package com.bayazidht.dongshinbuddy.api

import com.bayazidht.dongshinbuddy.model.GroqRequest
import com.bayazidht.dongshinbuddy.model.GroqResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") token: String, // এখানে "Bearer YOUR_KEY" পাঠাতে হবে
        @Body request: GroqRequest
    ): GroqResponse
}