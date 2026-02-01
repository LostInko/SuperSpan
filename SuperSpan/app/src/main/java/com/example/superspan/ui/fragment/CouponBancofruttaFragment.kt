package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.viewmodel.CouponType
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment di conferma per il coupon "Bancofrutta".
 * - Mostra l'immagine del coupon (stessa del banner).
 * - Chiede conferma con bottone "Attiva coupon".
 * - Se confermato, attiva il coupon e lo aggiunge alla barra espandibile.
 */
class CouponBancofruttaFragment : Fragment() {

    companion object {
        fun newInstance(): CouponBancofruttaFragment = CouponBancofruttaFragment()
    }

    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_bancofrutta, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Titolo
        view.findViewById<TextView>(R.id.tvTitle).text =
            getString(R.string.bancofrutta_title)

        // Immagine: stessa del banner
        view.findViewById<ImageView>(R.id.imgCouponPreview)
            .setImageResource(R.drawable.coupon_store_bancofrutta)

        // Back
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Bottone Annulla (torna indietro)
        view.findViewById<View>(R.id.btnCancel)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Bottone Conferma: attiva coupon e chiudi
        view.findViewById<View>(R.id.btnConfirm)?.setOnClickListener {
            // Evita doppia attivazione dello stesso tipo
            if (vm.hasActiveCouponOfType(CouponType.BANCOFRUTTA_DISCOUNT)) {
                showToast("Coupon sconto bancofrutta già attivo")
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return@setOnClickListener
            }

            it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)

            vm.markCouponActivated() // legacy flag per compatibilità/analytics
            vm.activateCoupon(
                type = CouponType.BANCOFRUTTA_DISCOUNT,
                title = getString(R.string.bancofrutta_title), // "Coupon sconto bancofrutta"
                detail = null
            )

            showToast(getString(R.string.bancofrutta_activated))
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}