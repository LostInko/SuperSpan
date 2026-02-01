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
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment "Pasta: 3 uguali, paghi 2".
 *
 * - Visualizza SOLO prodotti di PASTA.
 * - L'utente seleziona UNA sola pasta (tap = toggle).
 * - Bottom bar con 3 slot uguali: i primi 2 'pagati' (bordo rosso), il 3° neutro (omaggio).
 * - Pulsante "Conferma" abilitato quando c'è una selezione.
 */
class CouponPastaThreeForTwoFragment : Fragment() {

    companion object {
        fun newInstance(): CouponPastaThreeForTwoFragment = CouponPastaThreeForTwoFragment()
    }

    private lateinit var vm: HomeViewModel
    private lateinit var adapter: ThreeForOneAdapter

    private var selected: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_three_for_two, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<TextView>(R.id.tvTitle)?.text = getString(R.string.pasta_three_for_two_title)

        // Lista: solo PASTA
        val items = vm.products.value
            .orEmpty()
            .filter { it.category == ProductCategory.PASTA }

        // Recycler
        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        val spacingPx = (12 * view.resources.displayMetrics.density).toInt()
        rv.setPadding(spacingPx, 0, spacingPx, 0)
        rv.clipToPadding = false
        rv.addItemDecoration(GridSpacingItemDecoration(spanCount = 2, spacingPx = spacingPx, includeEdge = true))

        // Adapter
        adapter = ThreeForOneAdapter(
            items = items,
            isSelected = { p -> selected == p },
            isPaid = { p -> selected == p },  // evidenzia l'item scelto nella griglia
            onClick = { p -> onProductTap(p) }
        )
        rv.adapter = adapter

        // UI iniziale
        updateBottomBar(view)
        setupConfirmBar(view)
    }

    private fun onProductTap(p: Product) {
        selected = if (selected == p) null else p
        adapter.notifyDataSetChanged()
        view?.let {
            updateBottomBar(it)
            setupConfirmBar(it)
        }
    }

    private fun setupConfirmBar(root: View) {
        val confirmBar = root.findViewById<TextView>(R.id.btnConfirmBar)
        val enabled = (selected != null)

        confirmBar.isEnabled = enabled
        confirmBar.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.bg_confirm_bar_enabled else R.drawable.bg_confirm_bar_disabled
        )
        confirmBar.text = getString(R.string.confirm)

        confirmBar.setOnClickListener {
            val chosen = selected
            if (chosen != null) {
                it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                vm.markCouponActivated()
                vm.activateCoupon(
                    type = com.example.superspan.viewmodel.CouponType.PASTA_THREE_FOR_TWO,
                    title = getString(R.string.pasta_three_for_two_title), // "Pasta • 3×2 (stesso prodotto)"
                    detail = chosen.name
                )
                Toast.makeText(requireContext(), "Coupon confermato: ${chosen.name}", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Seleziona un tipo di pasta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBottomBar(root: View) {
        val slots = listOf(
            root.findViewById<ImageView>(R.id.slot1),
            root.findViewById<ImageView>(R.id.slot2),
            root.findViewById<ImageView>(R.id.slot3),
        )

        // Reset
        slots.forEach { slot ->
            slot.setImageResource(R.drawable.ic_question)
            slot.setBackgroundResource(R.drawable.bg_slot_neutral)
        }

        val sel = selected
        if (sel != null) {
            val imgRes = if (sel.imageRes != 0) sel.imageRes else R.drawable.ic_question

            // Stessa immagine per tutti e tre
            slots.forEach { it.setImageResource(imgRes) }

            // Slot 1 e 2: pagati (bordo rosso); Slot 3: omaggio (neutro)
            if (slots.isNotEmpty()) slots[0].setBackgroundResource(R.drawable.bg_slot_paid_red)
            if (slots.size > 1)    slots[1].setBackgroundResource(R.drawable.bg_slot_paid_red)
            if (slots.size > 2)    slots[2].setBackgroundResource(R.drawable.bg_slot_neutral)
        }
    }
}