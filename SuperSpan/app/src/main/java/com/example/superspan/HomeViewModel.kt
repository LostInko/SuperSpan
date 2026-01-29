package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.model.Order
import com.example.superspan.model.Product
import com.example.superspan.model.ProductCategory
import com.example.superspan.model.parsedPrice

class HomeViewModel : ViewModel() {

    // -- Prodotti & Carrello --
    val products = MutableLiveData<MutableList<Product>>()
    val cartTotal = MutableLiveData<Double>()

    // -- Indirizzi & selezione --
    val addresses = MutableLiveData<List<Address>>()
    val selectedAddress = MutableLiveData<Address?>()

    // -- Preferiti --
    val favorites = MutableLiveData<List<Product>>(emptyList())

    // -- Ordini --
    val orders = MutableLiveData<List<Order>>(emptyList())
    val selectedOrder = MutableLiveData<Order?>()

    // -- Coupon --
    val isAnyCouponActivated = MutableLiveData(false)
    val activatedCouponName = MutableLiveData<String?>(null)

    init {
        // Inizializza prodotti (con categorie)
        products.value = mutableListOf(
            Product("Succo ACE", "Brik 0.2L x 6", "1,75€", R.drawable.succo_ace, ProductCategory.BEVANDE),
            Product("Ichnusa non filtrata", "50cl", "1,56€", R.drawable.ichnusa_non_filtrata, ProductCategory.BEVANDE),
            Product("Latte Arborea", "1lt", "1,32€", R.drawable.latte_arborea, ProductCategory.BEVANDE),
            Product("Salsiccia classica stagionata", "All'etto", "2,03€", R.drawable.salsiccia_secca_murru, ProductCategory.AFFETTATI),
            Product("Ravioli ricotta e spinaci", "", "2,30€", R.drawable.ravioli_ricotta_cossu, ProductCategory.PASTA),
            Product("LOreal Invisifix", "100ml", "3,49€", R.drawable.gel_loreal, ProductCategory.CURA_PERSONALE),
            Product("Shampoo Classico H&S", "90ml", "2,64€", R.drawable.hes_shampoo, ProductCategory.CURA_PERSONALE),
            Product("Pantene Balsamo", "180ml", "2,19€", R.drawable.pantene_balsamo, ProductCategory.CURA_PERSONALE),
            Product("Garnier Metodo Ricci", "200ml", "4,49€", R.drawable.shampoo_garnier, ProductCategory.CURA_PERSONALE),
            Product("Cera Phenomenal", "100ml", "6,20€", R.drawable.cera_phenomenal, ProductCategory.CURA_PERSONALE)
        )

        cartTotal.value = 0.0

        // Carica indirizzi
        loadAddresses()
    }

    // -------------------------
    //         INDIRIZZI
    // -------------------------
    fun loadAddresses() {
        val list = listOf(
            Address("Cagliari", "Via del Nastro Azzurro 17", "09131", "Casa Mia", isSelected = true),
            Address("Cagliari", "Via Bruxelles 13", "09129", "Casa di Alice", isSelected = false)
        )
        addresses.value = list
        // Aggiorna selectedAddress coerentemente allo stato isSelected
        selectedAddress.value = list.find { it.isSelected }
    }

    fun getSelectedAddress(): Address? =
        addresses.value?.find { it.isSelected }

    fun selectAddress(selected: Address) {
        val currentList = addresses.value ?: return
        val newList = currentList.map { address ->
            address.copy(isSelected = (address == selected))
        }
        addresses.value = newList
        selectedAddress.value = selected
    }

    // -------------------------
    //         CARRELLO
    // -------------------------
    fun updateCartTotal() {
        val list = products.value.orEmpty()
        val total: Double = list.sumOf { product ->
            product.parsedPrice() * product.qty.toDouble()
        }
        cartTotal.postValue(total)
    }

    fun clearCart() {
        val list = products.value ?: return
        list.forEach { product -> product.qty = 0 }
        notifyChange()
    }

    fun refreshProducts() {
        products.value = products.value // trigger observers
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
    }

    // -------------------------
    //         PREFERITI
    // -------------------------
    fun toggleFavoriteByRef(product: Product) {
        product.isFavorite = !product.isFavorite
        refreshProducts()
        recomputeFavorites()
    }

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

    // -------------------------
    //           ORDINI
    // -------------------------
    fun addOrder(newOrder: Order) {
        val currentOrders = orders.value.orEmpty().toMutableList()
        currentOrders.add(newOrder)
        orders.value = currentOrders
    }

    fun selectOrder(order: Order) {
        selectedOrder.value = order
    }

    // -------------------------
    //           COUPON
    // -------------------------
    /** Imposta direttamente nome e stato attivo del coupon. */
    fun activateCoupon(name: String) {
        isAnyCouponActivated.value = true
        activatedCouponName.value = name
    }

    /** Solo il flag (usato nel 3×1 dopo conferma). */
    fun markCouponActivated() {
        isAnyCouponActivated.value = true
    }

    /** Disattiva qualsiasi coupon e pulisce il nome. */
    fun clearActivatedCoupon() {
        isAnyCouponActivated.value = false
        activatedCouponName.value = null
    }
}