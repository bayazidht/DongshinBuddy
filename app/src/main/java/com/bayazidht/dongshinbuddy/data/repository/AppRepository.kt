package com.bayazidht.dongshinbuddy.data.repository

import com.bayazidht.dongshinbuddy.api.GroqService
import com.bayazidht.dongshinbuddy.model.GroqRequest
import com.google.firebase.firestore.FirebaseFirestore

class AppRepository(
    private val groqService: GroqService,
    private val db: FirebaseFirestore
) {

    fun fetchVersions(onSuccess: (Map<String, Any>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy").document("version_control")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val versions = mutableMapOf<String, Any>()
                    versions["ai_context_v"] = document.getLong("ai_context_v")?.toInt() ?: 0
                    versions["chips_data_v"] = document.getLong("chips_data_v")?.toInt() ?: 0
                    versions["quick_links_v"] = document.getLong("quick_links_v")?.toInt() ?: 0
                    versions["app_version"] = document.getString("app_version") ?: "1.0.0"
                    versions["force_update"] = document.getBoolean("force_update") ?: false
                    onSuccess(versions)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchAIConfig(onSuccess: (AIConfig) -> Unit) {
        db.collection("dongshin_buddy").document("ai_config")
            .get()
            .addOnSuccessListener { document ->
                val config = document.toObject(AIConfig::class.java) ?: AIConfig()
                onSuccess(config)
            }
            .addOnFailureListener { onSuccess(AIConfig()) }
    }

    fun fetchAiContext(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy").document("ai_context").get()
            .addOnSuccessListener { onSuccess(it.getString("context") ?: "") }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchChipsInfo(onSuccess: (List<String>, List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy").document("chips_data").get()
            .addOnSuccessListener { document ->
                val q = document.get("questions") as? List<String> ?: emptyList()
                val s = document.get("suggestions") as? List<String> ?: emptyList()
                onSuccess(q, s)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchQuickLinks(onSuccess: (List<Map<String, String>>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("dongshin_buddy").document("quick_links").get()
            .addOnSuccessListener { document ->
                val linksList = mutableListOf<Map<String, String>>()
                val linksMap = document.get("links") as? Map<*, *>
                linksMap?.forEach { (_, value) ->
                    if (value is Map<*, *>) {
                        val data = mapOf(
                            "title" to (value["title"]?.toString() ?: ""),
                            "url" to (value["url"]?.toString() ?: ""),
                            "icon" to (value["icon"]?.toString() ?: "")
                        )
                        linksList.add(data)
                    }
                }
                onSuccess(linksList)
            }
            .addOnFailureListener { onFailure(it) }
    }

    suspend fun getGroqResponse(apiKey: String, request: GroqRequest) =
        groqService.getCompletion(apiKey, request)
}