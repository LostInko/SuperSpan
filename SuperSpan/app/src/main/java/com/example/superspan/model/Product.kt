package com.example.superspan.model

import com.example.superspan.R

enum class ProductCategory(val label: String, val parentTab: String) {
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
    CURA_PERSONALE("Bellezza e Igiene", "Casa"),
    CURA_NEONATO("Infanzia e Neonati", "Casa"),
    PULIZIE("Cura della Casa", "Casa"),
    ANIMALI("Amici Animali", "Casa");

    companion object {
        fun getLabelsByTab(tabName: String): List<String> {
            return values().filter { it.parentTab == tabName || tabName == "Generale" }.map { it.label }
        }
    }
}

data class Product(
    val name: String,
    val description: String,
    val price: String,
    val imageRes: Int,
    val category: ProductCategory,
    var qty: Int = 0,
    var isFavorite: Boolean = false,
    var discountPrice: String? = null,
    var priceWhenAddedToFav: String? = null
)

// Helper per il calcolo (usato dal carrello e dai coupon)
fun Product.parsedPrice(): Double {
    val priceToParse = discountPrice ?: this.price
    return priceToParse.replace("€", "").replace(",", ".").trim().toDoubleOrNull() ?: 0.0
}

// Helper specifico per il confronto storico
fun Product.savedNumericPrice(): Double {
    return priceWhenAddedToFav?.replace("€", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: parsedPrice()
}