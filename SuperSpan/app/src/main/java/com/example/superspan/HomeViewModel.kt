package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.model.Product
import com.example.superspan.model.parsedPrice


class HomeViewModel : ViewModel() {

    val products = MutableLiveData<MutableList<Product>>()
    val cartTotal = MutableLiveData<Double>()

    // 1. Sposta la dichiarazione qui fuori (deve essere accessibile al Fragment)
    val addresses = MutableLiveData<List<Address>>()
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

        // 2. Chiamiamo la funzione per caricare gli indirizzi all'avvio
        loadAddresses()
    }

    // 3. Funzione per caricare i dati (Spostata fuori da init)
    fun loadAddresses() {
        val list = listOf(
            Address("Cagliari", "Via del Nastro Azzurro 17", "09131", "Casa Mia", isSelected = true),
            Address("Cagliari", "Via Bruxelles 13", "09129", "Casa di Alice", isSelected = true)
        )
        addresses.value = list
    }

    // 4. AGGIUNGI QUESTA: Gestisce il click sull'indirizzo
    fun selectAddress(selected: Address) {
        val currentList = addresses.value ?: return

        // Creiamo una nuova lista dove solo quello cliccato è true
        val newList = currentList.map { address ->
            // Se l'indirizzo della lista è quello selezionato, setta true, altrimenti false
            // (Funziona meglio se Address è una 'data class')
            address.copy(isSelected = (address == selected))
        }

        // Notifichiamo il cambiamento al Fragment
        addresses.value = newList
    }

    // --- Le tue funzioni esistenti rimangono uguali ---
    fun updateCartTotal() {
        val list = products.value.orEmpty()
        val total: Double = list.sumOf { product ->
            product.parsedPrice().toDouble() * product.qty.toDouble()
        }
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

    fun notifyChange(){
        products.value = products.value
        updateCartTotal()
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