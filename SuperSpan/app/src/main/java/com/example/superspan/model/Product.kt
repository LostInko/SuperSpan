package com.example.superspan.model

data class Product(
    val name: String,
    val description: String,
    val price: String,
    val imageRes: Int,
    var qty: Int = 0,
    var isFavorite: Boolean = false
)

fun Product.parsedPrice(): Double =
    this.price
        .replace("€", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0
