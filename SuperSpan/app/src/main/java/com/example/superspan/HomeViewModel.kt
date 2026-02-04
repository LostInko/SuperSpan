package com.example.superspan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.model.Order
import com.example.superspan.model.Product
import com.example.superspan.model.ProductCategory
import com.example.superspan.model.parsedPrice
import com.example.superspan.ui.activity.GlobalData
import java.util.UUID

// =======================
//   COUPON: TIPI & MODEL
// =======================
enum class CouponType {
    THREE_FOR_ONE,           // 3×1 • Cura personale
    PASTA_THREE_FOR_TWO,     // Pasta • 3×2 (stesso prodotto)
    BANCOFRUTTA_DISCOUNT     // Coupon sconto bancofrutta
}

data class ActiveCoupon(
    val id: String = UUID.randomUUID().toString(),
    val type: CouponType,
    val title: String,         // Es. "3×1 • Cura personale"
    val detail: String? = null // Es. "Spaghetti n.5"
)

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

    // ======================
    //        COUPON
    // ======================
    // Nuova struttura: LISTA di coupon attivi
    private val _activeCoupons = MutableLiveData<List<ActiveCoupon>>(emptyList())
    val activeCoupons: MutableLiveData<List<ActiveCoupon>> get() = _activeCoupons

    // Campi legacy per compatibilità con UI precedente
    val isAnyCouponActivated = MutableLiveData(false)
    val activatedCouponName = MutableLiveData<String?>(null)

    init {
        // Inizializza prodotti (con categorie)
        products.value = mutableListOf(
            // --- BEVANDE ANALCOLICHE ---
            Product("Succo ACE", "Brik 0.2L x 6", "1,75€", R.drawable.succo_ace, ProductCategory.BEVANDE_ANALCOLICHE),
            Product("Acqua Naturale", "6 x 1.5L", "1,92€", R.drawable.acqua_naturale, ProductCategory.BEVANDE_ANALCOLICHE),
            Product("Coca Cola", "1.5L", "1,85€", R.drawable.coca_cola, ProductCategory.BEVANDE_ANALCOLICHE),
            Product("The al Limone", "1.5L", "1,45€", R.drawable.the_limone, ProductCategory.BEVANDE_ANALCOLICHE),

            // --- BEVANDE ALCOLICHE ---
            Product("Ichnusa non filtrata", "50cl", "1,56€", R.drawable.ichnusa_non_filtrata, ProductCategory.BEVANDE_ALCOLICHE),
            Product("Vino Vermentino", "75cl", "6,50€", R.drawable.vino_vermentino, ProductCategory.BEVANDE_ALCOLICHE),
            Product("Birra Moretti", "66cl", "1,20€", R.drawable.birra_moretti, ProductCategory.BEVANDE_ALCOLICHE),
            Product("Prosecco DOC", "75cl", "5,90€", R.drawable.prosecco, ProductCategory.BEVANDE_ALCOLICHE),

            // --- ORTOFRUTTA ---
            Product("Banane", "Al kg", "1,99€", R.drawable.banane, ProductCategory.FRUTTA_VERDURA),
            Product("Mele Gala", "Al kg", "2,15€", R.drawable.mele_gala, ProductCategory.FRUTTA_VERDURA),
            Product("Insalata Mista", "200g", "1,20€", R.drawable.insalata_mista, ProductCategory.FRUTTA_VERDURA),

            // --- CARNE ---
            Product("Petto di Pollo", "Al kg", "9,50€", R.drawable.petto_pollo, ProductCategory.CARNE),
            Product("Macinato Scelto", "500g", "5,40€", R.drawable.macinato_bovino, ProductCategory.CARNE),

            // --- PESCE ---
            Product("Bastoncini di Pesce", "Confezione 10", "3,50€", R.drawable.bastoncini_findus, ProductCategory.PESCE),
            Product("Salmone Affumicato", "100g", "4,20€", R.drawable.salmone_affumicato, ProductCategory.PESCE),

            // --- LATTICINI ---
            Product("Latte Arborea", "1lt", "1,32€", R.drawable.latte_arborea, ProductCategory.LATTICINI),
            Product("Yogurt Greco", "150g", "1,15€", R.drawable.yogurt_greco, ProductCategory.LATTICINI),
            Product("Parmigiano Reggiano", "250g", "5,90€", R.drawable.parmigiano, ProductCategory.LATTICINI),

            // --- SALUMI E FORMAGGI ---
            Product("Salsiccia stagionata", "100g", "2,03€", R.drawable.salsiccia_secca_murru, ProductCategory.AFFETTATI),
            Product("Prosciutto Crudo", "100g", "3,20€", R.drawable.prosciutto_crudo, ProductCategory.AFFETTATI),

            // --- PASTA ---
            Product("Ravioli ricotta e spinaci", "", "2,30€", R.drawable.ravioli_ricotta_cossu, ProductCategory.PASTA),
            Product("Spaghetti n.5", "500g", "0,99€", R.drawable.spaghetti_barilla, ProductCategory.PASTA),
            Product("Penne Rigate", "500g", "0,99€", R.drawable.penne_rigate, ProductCategory.PASTA),

            // --- SNACK E PATATINE ---
            Product("Patatine Classiche", "50g", "1,30€", R.drawable.patatine_classiche, ProductCategory.SNACK),
            Product("Taralli Pugliesi", "250g", "1,70€", R.drawable.taralli, ProductCategory.SNACK),

            // --- DOLCI E BISCOTTI ---
            Product("Biscotti Gocciole", "500g", "2,85€", R.drawable.gocciole, ProductCategory.DOLCI),
            Product("Kinder Cereali", "9 x 20g", "3,99€", R.drawable.kinder, ProductCategory.DOLCI),
            Product("Cornetti Classici", "Confezione x6", "2,20€", R.drawable.cornetti, ProductCategory.DOLCI),

            // --- BELLEZZA E IGIENE ---
            Product("LOreal Invisifix", "100ml", "3,49€", R.drawable.gel_loreal, ProductCategory.CURA_PERSONALE),
            Product("Shampoo H&S", "90ml", "2,64€", R.drawable.hes_shampoo, ProductCategory.CURA_PERSONALE),
            Product("Bagnoschiuma Dove", "500ml", "2,90€", R.drawable.dove_bagnoschiuma, ProductCategory.CURA_PERSONALE),
            Product("Pantene Balsamo","180ml", "2,19€", R.drawable.pantene_balsamo, ProductCategory.CURA_PERSONALE),
            Product("Garnier Metodo Ricci","200ml", "4,49€", R.drawable.shampoo_garnier, ProductCategory.CURA_PERSONALE),
            Product("Cera Phenomenal","100ml","6,20€", R.drawable.cera_phenomenal, ProductCategory.CURA_PERSONALE),

            // --- INFANZIA E NEONATI ---
            Product("Pannolini Taglia 4", "Pacco x24", "7,90€", R.drawable.pannolini, ProductCategory.CURA_NEONATO),
            Product("Omogeneizzato Manzo", "2 x 80g", "1,60€", R.drawable.omogeneizzato, ProductCategory.CURA_NEONATO),

            // --- CURA DELLA CASA ---
            Product("Detersivo Piatti", "1L", "1,40€", R.drawable.detersivo_piatti, ProductCategory.PULIZIE),
            Product("Scottex", "6 rotoli", "2,50€", R.drawable.scottex, ProductCategory.PULIZIE),

            // --- AMICI ANIMALI ---
            Product("Croccantini Gatto", "1.5kg", "4,80€", R.drawable.croccantini_gatto, ProductCategory.ANIMALI),
            Product("Cibo Cani Manzo", "400g", "1,10€", R.drawable.lattina_cane, ProductCategory.ANIMALI),
        )

        cartTotal.value = 0.0

        // Carica indirizzi
        applyRandomDiscounts()
        cartTotal.value = 0.0
        loadAddresses()
    }

    // ------------------
    // Sconta randomicamente i prodotti
    // ------------------
    private fun applyRandomDiscounts() {
        val currentList = products.value ?: return
        // Mischia la lista e prendi i primi 7
        val discountedProducts = currentList.shuffled().take(7)

        discountedProducts.forEach { product ->
            val original = product.parsedPrice()
            // Calcola uno sconto random tra 20% e 40%
            val factor = (80 - (0..20).random()) / 100.0
            val newPrice = original * factor

            // Formatta il prezzo con due decimali e virgola
            product.discountPrice = String.format("%.2f€", newPrice).replace(".", ",")
        }
    }

    // -------------------------
    //         INDIRIZZI
    // -------------------------
    fun loadAddresses() {
        // Prendiamo l'utente loggato dal tuo GlobalData
        val user = GlobalData.currentUser

        if (user != null) {
            // Carichiamo solo i SUOI indirizzi
            addresses.value = user.addresses
            selectedAddress.value = user.addresses.find { it.isSelected }
        }
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

    fun addAddress(newAddress: Address) {
        val user = GlobalData.currentUser ?: return
        user.addresses.forEach { it.isSelected = false }

        val addressToAdd = newAddress.copy(isSelected = true)
        user.addresses.add(addressToAdd)

        addresses.value = user.addresses
        selectedAddress.value = addressToAdd
    }

    // -------------------------
    //         CARRELLO
    // -------------------------
    fun updateCartTotal() {
        val list = products.value.orEmpty()
        val total: Double = list.sumOf { p: Product ->
            p.parsedPrice() * p.qty.toDouble()
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

    fun removeFromCart(product: Product) {
        val currentList = products.value ?: mutableListOf()

        currentList.remove(product)

        //resettiamo la quantità a 0
        product.qty = 0
        products.value = currentList

        notifyChange()
    }

    fun updateProductQuantity(product: Product, newQty: Int) {
        val currentProducts = products.value ?: return

        // Cerchiamo il prodotto e aggiorniamo la quantità
        currentProducts.find { it.name == product.name }?.let {
            it.qty = newQty
        }

        // Notifichiamo il cambiamento alla UI
        products.value = currentProducts

        notifyChange()
    }

    // -------------------------
    //         PREFERITI
    // -------------------------
    fun toggleFavoriteByRef(product: Product) {
        product.isFavorite = !product.isFavorite

        // NUOVO: Se viene aggiunto, salviamo il prezzo attuale come riferimento storico
        if (product.isFavorite) {
            product.priceWhenAddedToFav = product.discountPrice ?: product.price
        }

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

    /** Nuovo metodo: controlla se è già attivo un coupon di quel tipo. */
    fun hasActiveCouponOfType(type: CouponType): Boolean {
        val list = _activeCoupons.value ?: return false
        return list.any { it.type == type }
    }

    /** Nuovo metodo: attiva un coupon (evita duplicati per tipo). */
    fun activateCoupon(type: CouponType, title: String, detail: String? = null) {
        val current = _activeCoupons.value ?: emptyList()
        if (current.any { it.type == type }) return // blocca duplicati

        val updated = current + ActiveCoupon(type = type, title = title, detail = detail)
        _activeCoupons.value = updated

        // aggiorna campi legacy
        isAnyCouponActivated.value = updated.isNotEmpty()
        updateLegacyName()
    }

    /** Overload legacy: per compatibilità con chiamate vecchie `activateCoupon(name)` */
    fun activateCoupon(name: String) {
        // Prova a inferire il tipo dal titolo (best-effort, evita crash)
        val lower = name.lowercase()
        val inferredType: CouponType? = when {
            lower.contains("3×1") || lower.contains("3x1") || lower.contains("cura personale") -> CouponType.THREE_FOR_ONE
            lower.contains("3×2") || lower.contains("3x2") || lower.contains("pasta") -> CouponType.PASTA_THREE_FOR_TWO
            lower.contains("bancofrutta") -> CouponType.BANCOFRUTTA_DISCOUNT
            else -> null
        }

        if (inferredType != null) {
            activateCoupon(inferredType, name, null)
            return
        }

        // Fallback estrema: solo legacy fields (non aggiunge in lista)
        isAnyCouponActivated.value = true
        activatedCouponName.value = name
    }

    /** Legacy: flag usato prima; lo manteniamo (utile per analytics o compat). */
    fun markCouponActivated() {
        isAnyCouponActivated.value = true
        // verrà riallineato quando si chiama activateCoupon(type,...)
    }

    /** Rimuove uno specifico coupon per id. */
    fun removeCouponById(id: String) {
        val current = _activeCoupons.value ?: emptyList()
        val updated = current.filterNot { it.id == id }
        _activeCoupons.value = updated

        isAnyCouponActivated.value = updated.isNotEmpty()
        updateLegacyName()
    }

    /** Disattiva tutti i coupon. */
    fun clearActivatedCoupon() {
        _activeCoupons.value = emptyList()
        isAnyCouponActivated.value = false
        activatedCouponName.value = null
    }

    /** Aggiorna il nome singolo (compatibilità con UI precedente). */
    private fun updateLegacyName() {
        val first = _activeCoupons.value?.firstOrNull()
        activatedCouponName.value = when {
            first == null -> null
            first.detail.isNullOrBlank() -> first.title
            else -> "${first.title} — ${first.detail}"
        }
    }

}