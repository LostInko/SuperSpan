package com.example.superspan.model
enum class ProductCategory(val label: String, val parentTab: String) {
    // --- ALIMENTARI ---
    FRUTTA_VERDURA("Ortofrutta", "Alimentari"),
    CARNE("Carne", "Alimentari"),
    PESCE("Pesce", "Alimentari"),
    AFFETTATI("Salumi", "Alimentari"),
    LATTICINI("Latte, Yogurt e Formaggi", "Alimentari"),
    PASTA("Pasta", "Alimentari"),
    BEVANDE_ALCOLICHE("Bevande alcoliche", "Alimentari"),
    BEVANDE_ANALCOLICHE("Bevande analcoliche", "Alimentari"),
    SNACK("Snack e Patatine", "Alimentari"),
    DOLCI("Dolci e Biscotti", "Alimentari"),

    // --- CASA E PERSONA ---
    CURA_PERSONALE("Bellezza e Igiene", "Casa"),
    CURA_NEONATO("Infanzia e Neonati", "Casa"),
    PULIZIE("Cura della Casa", "Casa"),
    ANIMALI("Amici Animali", "Casa");

    companion object {
        // Funzione per ottenere tutte le label di una specifica macro-categoria
        fun getLabelsByTab(tabName: String): List<String> {
            return if (tabName == "Generale") {
                values().map { it.label }
            } else {
                values().filter { it.parentTab == tabName }.map { it.label }
            }
        }
    }
}

data class Product(
    val name: String,
    val description: String,
    val price: String, // Prezzo originale (es. "2,50€")
    val imageRes: Int,
    val category: ProductCategory,
    var qty: Int = 0,
    var isFavorite: Boolean = false,
    var discountPrice: String? = null // Nuovo: prezzo scontato (es. "1,99€")
)

fun Product.parsedPrice(): Double {
    val priceToParse = discountPrice ?: this.price
    return priceToParse
        .replace("€", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0
}