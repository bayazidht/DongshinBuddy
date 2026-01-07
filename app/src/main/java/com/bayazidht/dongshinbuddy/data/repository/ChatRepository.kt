package com.bayazidht.dongshinbuddy.data.repository

import com.bayazidht.dongshinbuddy.api.GroqService
import com.bayazidht.dongshinbuddy.model.GroqRequest
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository(
    private val groqService: GroqService,
    private val db: FirebaseFirestore
) {
    suspend fun getGroqResponse(apiKey: String, request: GroqRequest) =
        groqService.getCompletion(apiKey, request)

    fun fetchUniversityInfo(onSuccess: (String, Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy")
            .document("university_info")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val version = document.getLong("version")?.toInt() ?: 0
                    val context = document.getString("context") ?: ""
                    onSuccess(context, version)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}