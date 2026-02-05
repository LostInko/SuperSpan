package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.HapticFeedbackConstants
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
import com.example.superspan.adapter.ActiveCouponsAdapter
import com.example.superspan.viewmodel.CouponType
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Hub centrale della sezione Coupon.
 * Gestisce la vetrina delle offerte disponibili e la barra espandibile dei coupon attivati.
 */
class CouponSectionFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_coupon, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione ViewModel condiviso con l'Activity
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        setupPromotionGallery(view)
        setupActiveCouponsList(view)
        setupExpandableCouponBar(view)

        // Tasto info per il regolamento
        view.findViewById<View>(R.id.btnInfo).setOnClickListener {
            showInfoDialog()
        }
    }

    /**
     * Configura i listener per le card promozionali nella vetrina.
     * Gestisce il controllo di attivazione duplicata e la navigazione verso i fragment di selezione.
     */
    private fun setupPromotionGallery(view: View) {
        // 1. Bancofrutta: Navigazione verso attivazione diretta
        val imgBancofrutta = view.findViewById<ImageView>(R.id.imgCoupon1)
        imgBancofrutta.setImageResource(R.drawable.coupon_store_bancofrutta)
        imgBancofrutta.setOnClickListener {
            if (viewModel.hasActiveCouponOfType(CouponType.BANCOFRUTTA_DISCOUNT)) {
                showToast("Coupon sconto bancofrutta già attivo")
                return@setOnClickListener
            }
            navigateToFragment(CouponBancofruttaFragment.newInstance())
        }

        // 2. 3×1 Cura personale: Navigazione verso selezione multipla
        val img3x1 = view.findViewById<ImageView>(R.id.imgCoupon2)
        img3x1.setImageResource(R.drawable.coupon_store_3x1)
        img3x1.setOnClickListener {
            if (viewModel.hasActiveCouponOfType(CouponType.THREE_FOR_ONE)) {
                showToast("Coupon 3×1 Cura personale già attivo")
                return@setOnClickListener
            }
            navigateToFragment(CouponThreeForOneFragment.newInstance())
        }

        // 3. Pasta 3×2: Navigazione verso selezione singola
        val imgPasta = view.findViewById<ImageView>(R.id.imgCoupon3)
        imgPasta.setImageResource(R.drawable.coupon_store_pasta3x2)
        imgPasta.setOnClickListener {
            if (viewModel.hasActiveCouponOfType(CouponType.PASTA_THREE_FOR_TWO)) {
                showToast("Coupon 3×2 Pasta già attivo")
                return@setOnClickListener
            }
            navigateToFragment(CouponPastaThreeForTwoFragment.newInstance())
        }
    }

    /**
     * Inizializza il RecyclerView per i coupon già attivati dall'utente.
     * Gestisce la logica di rimozione tramite il ViewModel.
     */
    private fun setupActiveCouponsList(view: View) {
        val rvActive = view.findViewById<RecyclerView>(R.id.rvActiveCoupons)
        rvActive.layoutManager = LinearLayoutManager(requireContext())

        // Inizializzazione Adapter con callback per la rimozione del singolo coupon
        val barAdapter = ActiveCouponsAdapter(emptyList()) { coupon ->
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            viewModel.removeCouponById(coupon.id)
            showToast("Coupon rimosso")
        }
        rvActive.adapter = barAdapter

        // Osservazione della lista coupon nel ViewModel per aggiornare la UI in tempo reale
        viewModel.activeCoupons.observe(viewLifecycleOwner) { list ->
            val expandableBar = view.findViewById<View>(R.id.couponExpandableBar)

            // La barra è visibile solo se è presente almeno un coupon attivo
            expandableBar.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
            barAdapter.submit(list)

            // Quando la lista cambia (es. da 0 a 1), manteniamo la barra chiusa di default
            if (list.isNotEmpty()) collapseExpandableContent(view)
        }
    }

    /**
     * Gestisce la logica di espansione/collasso della barra coupon in basso.
     * Include la gestione delle icone (chevron) e animazioni alpha.
     */
    private fun setupExpandableCouponBar(root: View) {
        val header = root.findViewById<View>(R.id.couponExpandableHeader)
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron = root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)

        // Stato iniziale della chevron (chiusa punta verso l'alto)
        if (content.visibility != View.VISIBLE) {
            chevron.setImageResource(R.drawable.ic_chevron_up)
        }

        header.setOnClickListener {
            val isExpanding = content.visibility != View.VISIBLE
            if (isExpanding) {
                content.alpha = 0f
                content.visibility = View.VISIBLE
                content.animate().alpha(1f).setDuration(150).start()
                chevron.setImageResource(R.drawable.ic_chevron_down) // Aperta punta verso il basso
            } else {
                content.animate().alpha(0f).setDuration(120).withEndAction {
                    content.visibility = View.GONE
                    chevron.setImageResource(R.drawable.ic_chevron_up)
                }.start()
            }
        }
    }

    /** Navigazione standard verso un fragment di dettaglio con aggiunta al backstack. */
    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /** Mostra un dialog informativo con supporto Material Design. */
    private fun showInfoDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_coupon_info, null)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnOk).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    /** Forza lo stato chiuso della barra senza animazioni (reset stato). */
    private fun collapseExpandableContent(root: View) {
        val content = root.findViewById<View>(R.id.couponExpandedContent)
        val chevron = root.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.couponExpandableChevron)
        content.clearAnimation()
        content.visibility = View.GONE
        content.alpha = 1f
        chevron.setImageResource(R.drawable.ic_chevron_up)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}