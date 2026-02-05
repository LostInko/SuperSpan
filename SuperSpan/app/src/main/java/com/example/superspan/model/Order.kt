package com.example.superspan.model

data class Order(
    val orderNumber: Int = generateId(),
    val products: List<Product>,
    val address: Address,
    val shop: String
) {
    companion object {
        private var nextOrderId = 1
        fun generateId(): Int {
            return nextOrderId++
        }
    }
}