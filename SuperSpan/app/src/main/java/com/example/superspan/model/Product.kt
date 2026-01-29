package com.example.superspan.model
enum class ProductCategory(val label: String) {
    BEVANDE("Bevande"),
    CARNE("Carne"),
    AFFETTATI("Affettati"),
    PASTA("Pasta"),
    CURA_PERSONALE("Cura personale"),
    ALTRO("Altro")
}

data class Product(
    val name: String,
    val description: String,
    val price: String,
    val imageRes: Int,
    val category: ProductCategory,
    var qty: Int = 0,
    var isFavorite: Boolean = false
)

fun Product.parsedPrice(): Double =
    this.price
        .replace("€", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0
