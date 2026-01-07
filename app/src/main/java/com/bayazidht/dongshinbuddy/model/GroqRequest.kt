package com.bayazidht.dongshinbuddy.model

data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>
)