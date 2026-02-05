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
import com.example.superspan.viewmodel.CouponType
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment per l'offerta "Pasta 3x2".
 * Consente la selezione di un singolo tipo di pasta; l'offerta prevede
 * l'acquisto di 3 unità identiche al prezzo di 2.
 */
class CouponPastaThreeForTwoFragment : Fragment() {

    companion object {
        fun newInstance(): CouponPastaThreeForTwoFragment = CouponPastaThreeForTwoFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ThreeForOneAdapter

    // Stato locale: gestisce il prodotto singolo selezionato per l'offerta 3x2
    private var selectedProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recupero ViewModel condiviso per la comunicazione dello stato dei coupon
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_three_for_two, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader(view)
        setupRecyclerView(view)
        updateBottomBar(view)
        setupConfirmBar(view)
    }

    /** Configura l'intestazione e l'azione di ritorno. */
    private fun setupHeader(view: View) {
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.pasta_three_for_two_title)
    }

    /** Inizializza la lista prodotti filtrando esclusivamente la categoria PASTA. */
    private fun setupRecyclerView(view: View) {
        val items = viewModel.products.value.orEmpty()
            .filter { it.category == ProductCategory.PASTA }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val spacingPx = (12 * view.resources.displayMetrics.density).toInt()
        recyclerView.setPadding(spacingPx, 0, spacingPx, 0)
        recyclerView.clipToPadding = false
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingPx, true))

        adapter = ThreeForOneAdapter(
            items = items,
            isSelected = { p -> selectedProduct == p },
            isPaid = { p -> selectedProduct == p },
            onClick = { p -> handleSelection(p) }
        )
        recyclerView.adapter = adapter
    }

    /** Gestisce la selezione esclusiva (singolo prodotto) della pasta. */
    private fun handleSelection(product: Product) {
        selectedProduct = if (selectedProduct == product) null else product
        adapter.notifyDataSetChanged()

        view?.let {
            updateBottomBar(it)
            setupConfirmBar(it)
        }
    }

    /** Configura il pulsante di conferma finale dell'offerta. */
    private fun setupConfirmBar(root: View) {
        val confirmBar = root.findViewById<TextView>(R.id.btnConfirmBar)
        val isEnabled = (selectedProduct != null)

        confirmBar.isEnabled = isEnabled
        confirmBar.background = ContextCompat.getDrawable(
            requireContext(),
            if (isEnabled) R.drawable.bg_confirm_bar_enabled else R.drawable.bg_confirm_bar_disabled
        )
        confirmBar.text = getString(R.string.confirm)

        confirmBar.setOnClickListener {
            selectedProduct?.let { chosen ->
                it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)

                // Registrazione coupon nel ViewModel
                viewModel.markCouponActivated()
                viewModel.activateCoupon(
                    type = CouponType.PASTA_THREE_FOR_TWO,
                    title = getString(R.string.pasta_three_for_two_title),
                    detail = chosen.name // Indica quale pasta specifica è stata scelta
                )

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /** * Aggiorna gli slot visivi in basso.
     * Mostra 3 unità del prodotto scelto: 2 con bordo rosso (pagate) e 1 neutro (omaggio).
     */
    private fun updateBottomBar(root: View) {
        val slots = listOf(
            root.findViewById<ImageView>(R.id.slot1),
            root.findViewById<ImageView>(R.id.slot2),
            root.findViewById<ImageView>(R.id.slot3),
        )

        // Reset degli slot allo stato iniziale (vuoto)
        slots.forEach { slot ->
            slot.setImageResource(R.drawable.ic_question)
            slot.setBackgroundResource(R.drawable.bg_slot_neutral)
        }

        selectedProduct?.let { sel ->
            val imgRes = if (sel.imageRes != 0) sel.imageRes else R.drawable.ic_question

            // Applica l'immagine selezionata a tutti i 3 slot della promo
            slots.forEach { it.setImageResource(imgRes) }

            // Configurazione bordi: Primi due slot evidenziati come a pagamento
            slots.getOrNull(0)?.setBackgroundResource(R.drawable.bg_slot_paid_red)
            slots.getOrNull(1)?.setBackgroundResource(R.drawable.bg_slot_paid_red)
            // Terzo slot rimane neutro (omaggio)
            slots.getOrNull(2)?.setBackgroundResource(R.drawable.bg_slot_neutral)
        }
    }
}