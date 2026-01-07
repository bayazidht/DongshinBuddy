package com.bayazidht.dongshinbuddy

data class GroqResponse(
    val id: String,
    val choices: List<GroqChoice>
)