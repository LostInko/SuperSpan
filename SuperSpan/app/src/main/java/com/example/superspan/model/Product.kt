package com.example.superspan.model
enum class ProductCategory(val label: String) {
    // --- ALIMENTARI (Tab: Alimentari) ---
    FRUTTA_VERDURA("Ortofrutta"),
    CARNE("Carne"),
    PESCE("Pesce"),
    AFFETTATI("Salumi"),
    LATTICINI("Latte, Yogurt e Formaggi"),
    PASTA("Pasta"),
    BEVANDE_ALCOLICHE("Bevande alcoliche"),
    BEVANDE_ANALCOLICHE("Bevande analcoliche"),

    SNACK("Snack e Patatine"),
    DOLCI("Dolci e Biscotti"),

    // --- CASA E PERSONA (Tab: Casa) ---
    CURA_PERSONALE("Bellezza e Igiene"), // Shampoo, bagnoschiuma, etc.
    CURA_NEONATO("Infanzia e Neonati"),   // Pannolini, pappe
    PULIZIE("Cura della Casa"),           // Detersivi, carta igienica
    ANIMALI("Amici Animali"),             // Cibo per cani e gatti

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
