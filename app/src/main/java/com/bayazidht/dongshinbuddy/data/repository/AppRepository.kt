package com.bayazidht.dongshinbuddy.data.repository

import android.util.Log
import com.bayazidht.dongshinbuddy.api.GroqService
import com.bayazidht.dongshinbuddy.model.GroqRequest
import com.google.firebase.firestore.FirebaseFirestore

class AppRepository(
    private val groqService: GroqService,
    private val db: FirebaseFirestore
) {
    suspend fun getGroqResponse(apiKey: String, request: GroqRequest) =
        groqService.getCompletion(apiKey, request)

    fun fetchAIConfig(onSuccess: (AIConfig) -> Unit) {
        db.collection("dongshin_buddy").document("ai_config")
            .get()
            .addOnSuccessListener { document ->
                val config = document.toObject(AIConfig::class.java) ?: AIConfig()
                onSuccess(config)

                Log.d("FirebaseCheck", "Full Data: ${document.data}")
            }
            .addOnFailureListener { onSuccess(AIConfig()) }
    }

    fun fetchUniversityInfo(onSuccess: (String, Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy")
            .document("university_info")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val version = document.getLong("version")?.toInt() ?: 0
                    val context = document.getString("context") ?: ""
                    onSuccess(context, version)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchChipsInfo(onSuccess: (List<String>, List<String>, Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy")
            .document("chips_data")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val questions = document.get("questions") as? List<String> ?: emptyList()
                    val suggestions = document.get("suggestions") as? List<String> ?: emptyList()
                    val version = document.getLong("version")?.toInt() ?: 0
                    onSuccess(questions, suggestions, version)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchQuickLinks(onSuccess: (List<Map<String, String>>, Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy").document("quick_links")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val version = document.getLong("version")?.toInt() ?: 0
                    val linksList = mutableListOf<Map<String, String>>()

                    val linksMap = document.get("links") as? Map<*, *>

                    linksMap?.forEach { (_, value) ->
                        if (value is Map<*, *>) {
                            val linkData = mutableMapOf<String, String>()
                            linkData["title"] = value["title"]?.toString() ?: ""
                            linkData["url"] = value["url"]?.toString() ?: ""
                            linkData["icon"] = value["icon"]?.toString() ?: ""

                            if (linkData["title"]!!.isNotEmpty()) {
                                linksList.add(linkData)
                            }
                        }
                    }
                    onSuccess(linksList, version)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}