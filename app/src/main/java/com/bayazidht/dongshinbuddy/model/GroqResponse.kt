package com.bayazidht.dongshinbuddy.model

data class GroqResponse(
    val id: String,
    val choices: List<GroqChoice>
)