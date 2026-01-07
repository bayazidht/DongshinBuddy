package com.bayazidht.dongshinbuddy

data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)