package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.ui.ActiveCouponsAdapter
import com.example.superspan.viewmodel.CouponType
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Sezione coupon "in vetrina":
 * - Mostra tre card immagine (Bancofrutta, 3x1, Pasta 3x2).
 * - Clic su ciascuna card:
 *    - Se il tipo è già attivo -> feedback e non aprire nulla.
 *    - Altrimenti:
 *        - Bancofrutta: attivazione diretta (nessuna selezione).
 *        - 3x1: naviga al fragment di selezione multipla.
 *        - Pasta 3x2: naviga al fragment di selezione singola.
 * - Barra espandibile in basso:
 *    - Titolo fisso "Coupon attivi".
 *    - Contenuto: RecyclerView con TUTTI i coupon attivi (nome + QR + X per rimuovere).
 *    - Chevron coerente con barra in basso: chiusa = freccia su, aperta = freccia giù.
 */
class CouponSectionFragment : Fragment() {

    private lateinit var vm: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        // ------------------------------
        //  Vetrina: imposta le immagini
        // ------------------------------
        view.findViewById<ImageView>(R.id.imgCoupon1)
            .setImageResource(R.drawable.coupon_store_bancofrutta) // Coupon sconto bancofrutta
        view.findViewById<ImageView>(R.id.imgCoupon2)
            .setImageResource(R.drawable.coupon_store_3x1)         // 3×1 Cura personale
        view.findViewById<ImageView>(R.id.imgCoupon3)
            .setImageResource(R.drawable.coupon_store_pasta3x2)    // Pasta 3×2

        // ----------------------------------------------------
        //  1) Bancofrutta: attivazione DIRETTA senza selezione
        //     - Blocca duplicati per tipo
        //     - Attiva il coupon e mostra feedback
        // ----------------------------------------------------
        view.findViewById<ImageView>(R.id.imgCoupon1).setOnClickListener {
            if (vm.hasActiveCouponOfType(CouponType.BANCOFRUTTA_DISCOUNT)) {
                showToast("Coupon sconto bancofrutta già attivo")
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponBancofruttaFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }


        // ----------------------------------------------------
        //  2) 3×1 Cura personale:
        //     - Se già attivo -> feedback
        //     - Altrimenti -> naviga al fragment di selezione multipla
        // ----------------------------------------------------
        view.findViewById<ImageView>(R.id.imgCoupon2).setOnClickListener {
            if (vm.hasActiveCouponOfType(CouponType.THREE_FOR_ONE)) {
                showToast("Coupon 3×1 Cura personale già attivo")
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponThreeForOneFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // ----------------------------------------------------
        //  3) Pasta 3×2 (stesso prodotto):
        //     - Se già attivo -> feedback
        //     - Altrimenti -> naviga al fragment di selezione singola
        // ----------------------------------------------------
        view.findViewById<ImageView>(R.id.imgCoupon3).setOnClickListener {
            if (vm.hasActiveCouponOfType(CouponType.PASTA_THREE_FOR_TWO)) {
                showToast("Coupon 3×2 Pasta già attivo")
                return@setOnClickListener
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CouponPastaThreeForTwoFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // -------------------------
        //  Barra espandibile (UI)
        // -------------------------
        setupExpandableCouponBar(view)

        // ------------------------------------------------------------
        //  RecyclerView nella barra: mostra TUTTI i coupon attivi
        //  - LayoutManager verticale
        //  - Adapter con callback di rimozione per singola riga
        // ------------------------------------------------------------
        val rvActive = view.findViewById<RecyclerView>(R.id.rvActiveCoupons)
        rvActive.layoutManager = LinearLayoutManager(requireContext())
        val barAdapter = ActiveCouponsAdapter(emptyList()) { coupon ->
            // Rimozione del singolo coupon dalla barra
            view.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT)
            vm.removeCouponById(coupon.id)
            showToast("Coupon rimosso")
        }
        rvActive.adapter = barAdapter

        // ------------------------------------------------------------
        //  Observer: lista coupon attivi
        //  - Mostra/Nasconde la barra in base alla lista
        //  - Aggiorna i dati dell'adapter
        //  - Mantiene la barra chiusa di default quando c'è almeno un coupon
        // ------------------------------------------------------------
        vm.activeCoupons.observe(viewLifecycleOwner) { list ->
            val bar = view.findViewById<View>(R.id.couponExpandableBar)
            bar.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
            barAdapter.submit(list)
            if (list.isNotEmpty()) collapseExpandableContent(view) // rimani chiuso by default
        }

        view.findViewById<View>(R.id.btnInfo).setOnClickListener {
            showInfoDialog()
        }
    }


    /**
     * Mostra un pop-up informativo sulle modalità di utilizzo dei coupon
     */
    private fun showInfoDialog() {
        // 1. Inflate del layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_coupon_info, null)

        // 2. Creazione del Dialog con MaterialAlertDialogBuilder per supporto bordi arrotondati
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // 3. Gestione del tasto di chiusura
        dialogView.findViewById<View>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        // Rendi lo sfondo del container del dialog trasparente per far vedere i bordi arrotondati del layout (se applicati)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    /**
     * Gestione dell'header della barra espandibile.
     * - Chevron coerente con barra in basso:
     *    chiusa = ic_chevron_up (indica "apri verso l'alto")
     *    aperta = ic_chevron_down (indica "chiudi verso il basso")
     */
    private fun setupExpandableCouponBar(root: View) {
        val header = root.findViewById<View>(R.id.couponExpandableHeader)
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron = root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)

        // Stato iniziale: se il contenuto è nascosto, chevron = UP
        if (content.visibility != View.VISIBLE) {
            chevron.setImageResource(R.drawable.ic_chevron_up) // barra in basso -> apri su
        }

        header.setOnClickListener {
            val expanding = content.visibility != View.VISIBLE
            if (expanding) {
                // Animazione fade-in del contenuto
                content.alpha = 0f
                content.visibility = View.VISIBLE
                content.animate().alpha(1f).setDuration(150).start()
                chevron.setImageResource(R.drawable.ic_chevron_down) // aperta -> giù (chiudi)
            } else {
                // Animazione fade-out del contenuto
                content.animate().alpha(0f).setDuration(120).withEndAction {
                    content.visibility = View.GONE
                    chevron.setImageResource(R.drawable.ic_chevron_up) // chiusa -> su (apri)
                }.start()
            }
        }
    }

    /**
     * Forza lo stato "chiuso" della barra (senza animazione).
     * - Utile quando la lista di coupon passa da 0 -> N (mostrando la barra).
     */
    private fun collapseExpandableContent(root: View) {
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron = root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)
        content.clearAnimation()
        content.visibility = View.GONE
        content.alpha = 1f
        chevron.setImageResource(R.drawable.ic_chevron_up)
    }

    /** Toast helper */
    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}