package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.ThreeForOneAdapter
import com.example.superspan.model.Product
import com.example.superspan.model.ProductCategory
import com.example.superspan.model.parsedPrice
import com.example.superspan.viewmodel.CouponType
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment per l'offerta promozionale "3x1 Cura Personale".
 * Consente la selezione di esattamente 3 prodotti della categoria CURA_PERSONALE.
 * Applica una logica visiva per cui il prodotto più costoso viene evidenziato come "pagato".
 */
class CouponThreeForOneFragment : Fragment() {

    companion object {
        fun newInstance(): CouponThreeForOneFragment = CouponThreeForOneFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ThreeForOneAdapter

    // Stato locale per gestire la selezione temporanea prima della conferma
    private val selectedItems = mutableListOf<Product>()
    private var paidProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Accesso al ViewModel condiviso con l'Activity per gestire lo stato dei coupon
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_three_for_one, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader(view)
        setupRecyclerView(view)
        updateBottomBar(view)
        setupConfirmBar(view)
    }

    /** Configura i componenti della testata e il pulsante di ritorno. */
    private fun setupHeader(view: View) {
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<TextView>(R.id.tvTitle)?.text = "3×1 • Cura personale"
    }

    /** Configura la griglia dei prodotti filtrando per la categoria specifica. */
    private fun setupRecyclerView(view: View) {
        // Filtraggio dati: mostriamo solo prodotti appartenenti alla categoria Bellezza/Igiene
        val items = viewModel.products.value.orEmpty()
            .filter { it.category == ProductCategory.CURA_PERSONALE }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Configurazione spaziatura dinamica per la griglia
        val spacingPx = (12 * view.resources.displayMetrics.density).toInt()
        recyclerView.setPadding(spacingPx, 0, spacingPx, 0)
        recyclerView.clipToPadding = false
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingPx, true))

        adapter = ThreeForOneAdapter(
            items = items,
            isSelected = { p -> selectedItems.contains(p) },
            isPaid = { p -> paidProduct == p },
            onClick = { p -> handleProductSelection(p) }
        )
        recyclerView.adapter = adapter
    }

    /**
     * Gestisce il toggle della selezione:
     * - Rimuove se già presente.
     * - Aggiunge se non presente (fino a un massimo di 3).
     */
    private fun handleProductSelection(product: Product) {
        if (selectedItems.contains(product)) {
            selectedItems.remove(product)
        } else {
            if (selectedItems.size >= 3) {
                Toast.makeText(requireContext(), "Massimo 3 prodotti", Toast.LENGTH_SHORT).show()
                return
            }
            selectedItems.add(product)
        }

        recomputePriceLogic()
        adapter.notifyDataSetChanged()

        view?.let {
            updateBottomBar(it)
            setupConfirmBar(it)
        }
    }

    /** Individua il prodotto con il prezzo maggiore tra i selezionati per l'evidenziazione visiva. */
    private fun recomputePriceLogic() {
        paidProduct = selectedItems.maxByOrNull { it.parsedPrice() }
    }

    /** Aggiorna lo stato della barra di conferma (abilitata solo con 3 prodotti selezionati). */
    private fun setupConfirmBar(root: View) {
        val confirmBar = root.findViewById<TextView>(R.id.btnConfirmBar)
        val isReady = selectedItems.size == 3

        confirmBar.isEnabled = isReady
        confirmBar.background = ContextCompat.getDrawable(
            requireContext(),
            if (isReady) R.drawable.bg_confirm_bar_enabled else R.drawable.bg_confirm_bar_disabled
        )

        confirmBar.setOnClickListener {
            if (isReady) {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                viewModel.markCouponActivated()
                viewModel.activateCoupon(
                    type = CouponType.THREE_FOR_ONE,
                    title = "3×1 • Cura personale",
                    detail = null
                )
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /** Mostra le anteprime dei prodotti selezionati negli slot della barra inferiore. */
    private fun updateBottomBar(root: View) {
        val slots = listOf(
            root.findViewById<ImageView>(R.id.slot1),
            root.findViewById<ImageView>(R.id.slot2),
            root.findViewById<ImageView>(R.id.slot3),
        )

        // Reset visuale degli slot
        slots.forEach { slot ->
            slot.setImageResource(R.drawable.ic_question)
            slot.setBackgroundResource(R.drawable.bg_slot_neutral)
        }

        // Popolamento dinamico in base alla selezione attuale
        selectedItems.forEachIndexed { i, product ->
            if (i < slots.size) {
                slots[i].setImageResource(if (product.imageRes != 0) product.imageRes else R.drawable.ic_question)
                // Applica bordo rosso solo al prodotto identificato come "a pagamento"
                val isPaid = (product == paidProduct)
                slots[i].setBackgroundResource(
                    if (isPaid) R.drawable.bg_slot_paid_red else R.drawable.bg_slot_neutral
                )
            }
        }
    }
}

/**
 * Decoratore per RecyclerView per gestire i margini uniformi tra gli elementi della griglia.
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position < spanCount) outRect.top = spacing
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) outRect.top = spacing
        }
    }
}