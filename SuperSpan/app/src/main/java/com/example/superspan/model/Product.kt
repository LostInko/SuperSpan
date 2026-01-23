package com.example.superspan.model

class Product (
    val name: String,
    val description: String,
    val price: String,
    val imageRes: Int,
    var qty: Int = 0
)


fun Product.parsedPrice(): Double =
    this.price
        .replace("€", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0

