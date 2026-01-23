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

    init {
        // inizializzi qui solo una volta
        products.value = mutableListOf(
            Product("Succo ACE", "Brik 0.2L x 6", "1,75€", R.drawable.succo_ace),
            Product("Ichnusa non filtrata","50cl", "1,56€", R.drawable.ichnusa_non_filtrata),
            Product("Latte Arborea","1lt", "1,32€", R.drawable.latte_arborea),
            Product("Salsiccia classica stagionata","All'etto", "2,03€", R.drawable.salsiccia_secca_murru),
            Product("Ravioli ricotta e spinaci","", "2,30€", R.drawable.ravioli_ricotta_cossu)
        )

        cartTotal.value = 0.0
    }


    fun updateCartTotal() {
        val list = products.value.orEmpty()
        val total: Double = list.sumOf { product ->
            product.parsedPrice().toDouble() * product.qty.toDouble()
        }
        cartTotal.postValue(total)
    }

}
