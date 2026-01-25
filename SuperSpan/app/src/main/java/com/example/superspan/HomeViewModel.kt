package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.Product
import com.example.superspan.model.parsedPrice

class HomeViewModel : ViewModel() {

    // Lista prodotti che NON si resetta cambiando fragment
    val products = MutableLiveData<MutableList<Product>>()

    // Totale carrello aggiornato
    val cartTotal = MutableLiveData<Double>()

    // ⬇️ NEW: LiveData con i preferiti (derivata)
    val favorites = MutableLiveData<List<Product>>(emptyList())

    init {
        products.value = mutableListOf(
            Product("Succo ACE", "Brik 0.2L x 6", "1,75€", R.drawable.succo_ace),
            Product("Ichnusa non filtrata","50cl", "1,56€", R.drawable.ichnusa_non_filtrata),
            Product("Latte Arborea","1lt", "1,32€", R.drawable.latte_arborea),
            Product("Salsiccia classica stagionata","All'etto", "2,03€", R.drawable.salsiccia_secca_murru),
            Product("Ravioli ricotta e spinaci","", "2,30€", R.drawable.ravioli_ricotta_cossu)
        )
        cartTotal.value = 0.0
        recomputeFavorites()
    }

    fun updateCartTotal() {
        val list = products.value.orEmpty()
        val total: Double = list.sumOf { p -> p.parsedPrice() * p.qty.toDouble() }
        cartTotal.postValue(total)
    }

    fun refreshProducts() {
        products.value = products.value
    }

    fun setQtyAt(index: Int, qty: Int) {
        val list = products.value ?: return
        if (index in list.indices) {
            list[index].qty = qty.coerceAtLeast(0)
            updateCartTotal()
            refreshProducts()
        }
    }

    fun notifyChange() {
        products.value = products.value
        updateCartTotal()
        recomputeFavorites()
    }

    // ⬇️ NEW: toggle preferito
    fun toggleFavoriteByRef(product: Product) {
        product.isFavorite = !product.isFavorite
        refreshProducts()
        recomputeFavorites()
    }

    // ⬇️ NEW: toggle per indice o per nome se serve
    fun toggleFavoriteByIndex(index: Int) {
        val list = products.value ?: return
        if (index in list.indices) {
            list[index].isFavorite = !list[index].isFavorite
            refreshProducts()
            recomputeFavorites()
        }
    }

    fun toggleFavoriteByName(name: String) {
        val p = products.value?.find { it.name == name } ?: return
        p.isFavorite = !p.isFavorite
        refreshProducts()
        recomputeFavorites()
    }

    private fun recomputeFavorites() {
        favorites.postValue(products.value?.filter { it.isFavorite } ?: emptyList())
    }
}

