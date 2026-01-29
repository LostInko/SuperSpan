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
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment "3×1" (seleziona 3 prodotti - paghi il più costoso).
 *
 * - Visualizza SOLO prodotti di CURA_PERSONALE.
 * - L'utente può selezionare fino a 3 prodotti (tap per selezionare/deselezionare).
 * - Non usiamo badge o testi "Paghi/Omaggio": SOLO bordo rosso nello slot pagato.
 * - Barra di conferma full-width con testo "Conferma".
 */
class CouponThreeForOneFragment : Fragment() {

    companion object {
        fun newInstance(): CouponThreeForOneFragment = CouponThreeForOneFragment()
    }

    private lateinit var vm: HomeViewModel
    private lateinit var adapter: ThreeForOneAdapter

    // Stato locale: prodotti selezionati e prodotto "pagato" (il più costoso)
    private val selected = mutableListOf<Product>()
    private var paidProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_three_for_one, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Header: back + titolo ---
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<TextView>(R.id.tvTitle)?.text = "3×1 • Cura personale"

        // --- Lista: solo CURA_PERSONALE ---
        val items = vm.products.value
            .orEmpty()
            .filter { it.category == ProductCategory.CURA_PERSONALE }

        // RecyclerView: 2 colonne, spaziatura uniforme e padding laterale per centratura visiva
        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        val glm = GridLayoutManager(requireContext(), 2)
        rv.layoutManager = glm

        // Spaziatura in dp -> px
        val spacingPx = (12 * view.resources.displayMetrics.density).toInt()
        rv.setPadding(spacingPx, 0, spacingPx, 0)
        rv.clipToPadding = false
        rv.addItemDecoration(GridSpacingItemDecoration(spanCount = 2, spacingPx = spacingPx, includeEdge = true))

        // Adapter: nessun bottone interno, tap sull'item/immagine per il toggle
        adapter = ThreeForOneAdapter(
            items = items,
            isSelected = { p -> selected.contains(p) },
            isPaid = { p -> paidProduct == p },
            onClick = { p -> onProductTap(p) } // toggling
        )
        rv.adapter = adapter

        // --- UI iniziale: bottom bar + barra conferma ---
        updateBottomBar(view)
        setupConfirmBar(view)
    }

    /**
     * Toggle selezione:
     * - se già selezionato -> rimuovi
     * - se non selezionato -> aggiungi (max 3)
     * Aggiorna prodotto pagato (il più costoso), lista e UI di supporto.
     */
    private fun onProductTap(p: Product) {
        if (selected.contains(p)) {
            selected.remove(p)
        } else {
            if (selected.size == 3) {
                Toast.makeText(requireContext(), "Puoi selezionare massimo 3 prodotti", Toast.LENGTH_SHORT).show()
                return
            }
            selected.add(p)
        }
        recomputePaid()
        adapter.notifyDataSetChanged()
        view?.let {
            updateBottomBar(it)
            setupConfirmBar(it)
        }
    }

    /** Ricalcola quale dei selezionati è "pagato": quello con prezzo più alto. */
    private fun recomputePaid() {
        paidProduct = selected.maxByOrNull { it.parsedPrice() }
    }

    /**
     * Barra di conferma full-width:
     * - Abilitata solo con 3 selezionati
     * - Colori/sfondo diversi per stato enabled/disabled
     */
    private fun setupConfirmBar(root: View) {
        val confirmBar = root.findViewById<TextView>(R.id.btnConfirmBar)
        val enabled = selected.size == 3

        confirmBar.isEnabled = enabled
        confirmBar.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.bg_confirm_bar_enabled else R.drawable.bg_confirm_bar_disabled
        )
        confirmBar.text = "Conferma"

        confirmBar.setOnClickListener {
            if (selected.size == 3) {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                vm.markCouponActivated()
                vm.activateCoupon("3×1 • Cura personale")
                Toast.makeText(requireContext(), "Coupon confermato", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                // Non dovrebbe accadere, è disabilitato
                Toast.makeText(requireContext(), "Seleziona 3 prodotti", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Aggiorna la bottom bar:
     * - Mostra i 3 slot con l'immagine del prodotto selezionato (placeholder se mancante)
     * - Solo il "pagato" ha bordo rosso; gli altri bordo neutro.
     * - Nessun badge o testo.
     */
    private fun updateBottomBar(root: View) {
        val slots = listOf(
            root.findViewById<ImageView>(R.id.slot1),
            root.findViewById<ImageView>(R.id.slot2),
            root.findViewById<ImageView>(R.id.slot3),
        )

        // Reset: placeholder + bordo neutro
        slots.forEach { slot ->
            slot.setImageResource(R.drawable.ic_question)
            slot.setBackgroundResource(R.drawable.bg_slot_neutral)
        }

        // Riempie gli slot in ordine di selezione
        selected.forEachIndexed { i, p ->
            if (i < slots.size) {
                val imgRes = if (p.imageRes != 0) p.imageRes else R.drawable.ic_question
                slots[i].setImageResource(imgRes)
                val isPaid = (p == paidProduct)
                slots[i].setBackgroundResource(
                    if (isPaid) R.drawable.bg_slot_paid_red else R.drawable.bg_slot_neutral
                )
            }
        }
    }
}

/**
 * ItemDecoration per spaziatura uniforme nelle griglie:
 * - includeEdge=true applica spacing anche ai bordi esterni -> effetto visivo centrato
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingPx: Int,
    private val includeEdge: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // posizione dell'item
        val column = position % spanCount                   // colonna (0..spanCount-1)

        if (includeEdge) {
            outRect.left = spacingPx - column * spacingPx / spanCount    // spazio a sinistra
            outRect.right = (column + 1) * spacingPx / spanCount         // spazio a destra
            if (position < spanCount) outRect.top = spacingPx            // top per la prima riga
            outRect.bottom = spacingPx                                   // bottom per tutte
        } else {
            outRect.left = column * spacingPx / spanCount
            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount
            if (position >= spanCount) outRect.top = spacingPx
        }
    }
}