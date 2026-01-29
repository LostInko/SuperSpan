package com.example.superspan.model

data class Application (
    val id: Int = generateId(),
    val name: String,

    val userId : String,
    val offerId: Int,
    val risposte: String
) {
    companion object {
        private var nextOrderId = 1

        fun generateId(): Int {
            return nextOrderId++
        }
    }
}