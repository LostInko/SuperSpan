package com.example.superspan.model

/**
 * Definisce le categorie merceologiche e la loro appartenenza ai tab principali.
 */
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
        /**
         * Ritorna le etichette delle categorie filtrate per il tab selezionato (es. Alimentari o Casa).
         */
        fun getLabelsByTab(tabName: String): List<String> {
            return values().filter { it.parentTab == tabName || tabName == "Generale" }.map { it.label }
        }
    }
}

/**
 * Rappresenta un prodotto dell'app SuperSpan.
 * Include campi per gestire lo stato dei preferiti e il prezzo storico per Michele Giraud.
 */
data class Product(
    val name: String,
    val description: String,
    val price: String, // Prezzo di listino (es. "2,50€")
    val imageRes: Int,
    val category: ProductCategory,
    var qty: Int = 0,
    var isFavorite: Boolean = false,
    var discountPrice: String? = null, // Prezzo in offerta se presente
    var priceWhenAddedToFav: String? = null // Prezzo memorizzato al momento del "cuoricino"
)

// --- EXTENSION FUNCTIONS PER IL CALCOLO ---

/**
 * Converte la stringa del prezzo attuale (scontato o pieno) in un valore numerico Double.
 * Gestisce la rimozione del simbolo € e la conversione della virgola decimale.
 */
fun Product.parsedPrice(): Double {
    val priceToParse = discountPrice ?: this.price
    return priceToParse.cleanPriceToDouble()
}

/**
 * Converte la stringa del prezzo salvato nei preferiti in valore numerico.
 * Se non è presente un prezzo salvato, restituisce il prezzo attuale come fallback.
 */
fun Product.savedNumericPrice(): Double {
    return priceWhenAddedToFav?.cleanPriceToDouble() ?: parsedPrice()
}

/**
 * Funzione di utilità interna per evitare ripetizioni nella pulizia delle stringhe prezzo.
 */
private fun String.cleanPriceToDouble(): Double {
    return this.replace("€", "")
        .replace(",", ".")
        .trim()
        .toDoubleOrNull() ?: 0.0
}