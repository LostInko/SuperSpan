package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.model.Product
import com.example.superspan.model.ProductCategory
import com.example.superspan.model.parsedPrice


class HomeViewModel : ViewModel() {

    val products = MutableLiveData<MutableList<Product>>()
    val cartTotal = MutableLiveData<Double>()

    val addresses = MutableLiveData<List<Address>>()

    val selectedAddress = MutableLiveData<Address?>()
    val favorites = MutableLiveData<List<Product>>(emptyList())

    val isAnyCouponActivated = MutableLiveData(false)
    val activatedCouponName = MutableLiveData<String?>(null)

    init {
        products.value = mutableListOf(
            Product("Succo ACE", "Brik 0.2L x 6", "1,75€", R.drawable.succo_ace, ProductCategory.BEVANDE),
            Product("Ichnusa non filtrata","50cl", "1,56€", R.drawable.ichnusa_non_filtrata, ProductCategory.BEVANDE),
            Product("Latte Arborea","1lt", "1,32€", R.drawable.latte_arborea, ProductCategory.BEVANDE),
            Product("Salsiccia classica stagionata","All'etto", "2,03€", R.drawable.salsiccia_secca_murru, ProductCategory.AFFETTATI),
            Product("Ravioli ricotta e spinaci","", "2,30€", R.drawable.ravioli_ricotta_cossu, ProductCategory.PASTA),
            Product("LOreal Invisifix","100ml", "3,49€", R.drawable.gel_loreal, ProductCategory.CURA_PERSONALE),
            Product("Shampoo Classico H&S","90ml", "2,64€", R.drawable.hes_shampoo, ProductCategory.CURA_PERSONALE),
            Product("Pantene Balsamo","180ml", "2,19€", R.drawable.pantene_balsamo, ProductCategory.CURA_PERSONALE),
            Product("Garnier Metodo Ricci","200ml", "4,49€", R.drawable.shampoo_garnier, ProductCategory.CURA_PERSONALE),
            Product("Cera Phenomenal","100ml","6,20€", R.drawable.cera_phenomenal, ProductCategory.CURA_PERSONALE)
        )

        cartTotal.value = 0.0

        // 2. Chiamiamo la funzione per caricare gli indirizzi all'avvio
        loadAddresses()
    }

    // 3. Funzione per caricare i dati (Spostata fuori da init)
    fun loadAddresses() {
        val list = listOf(
            Address("Cagliari", "Via del Nastro Azzurro 17", "09131", "Casa Mia", isSelected = true),
            Address("Cagliari", "Via Bruxelles 13", "09129", "Casa di Alice", isSelected = false)
        )
        addresses.value = list
    }

    fun getSelectedAddress(): Address? {
        // Cerchiamo nella lista degli indirizzi quello che ha isSelected == true
        return addresses.value?.find { it.isSelected }
    }

    fun selectAddress(selected: Address) {
        val currentList = addresses.value ?: return
        val newList = currentList.map { address ->
            address.copy(isSelected = (address == selected))
        }
        addresses.value = newList

        selectedAddress.value = selected
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



    fun activateCoupon(name: String) {
        isAnyCouponActivated.value = true
        activatedCouponName.value = name
    }

    fun clearActivatedCoupon() {
        isAnyCouponActivated.value = false
        activatedCouponName.value = null
    }

    fun notifyChange(){
        products.value = products.value
        updateCartTotal()
    }

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

    fun markCouponActivated() {
        isAnyCouponActivated.value = true
    }
    private fun recomputeFavorites() {
        favorites.postValue(products.value?.filter { it.isFavorite } ?: emptyList())
    }
}