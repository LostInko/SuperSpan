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
 * Fragment per la gestione dell'attivazione del coupon "Bancofrutta".
 * Gestisce la visualizzazione della promo e l'inserimento nel sistema dei coupon attivi.
 */
class CouponBancofruttaFragment : Fragment() {

    companion object {
        /** Factory method per creare un'istanza del fragment. */
        fun newInstance(): CouponBancofruttaFragment = CouponBancofruttaFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recupero dell'istanza condivisa del ViewModel legata all'Activity
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_coupon_bancofrutta, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurazione UI: Inserimento testi e risorse grafiche
        view.findViewById<TextView>(R.id.tvTitle).text = getString(R.string.bancofrutta_title)
        view.findViewById<ImageView>(R.id.imgCouponPreview).setImageResource(R.drawable.coupon_store_bancofrutta)

        // Gestione navigazione: Pulsante di ritorno
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Azione Annulla: Ritorno alla schermata precedente con feedback aptico
        view.findViewById<View>(R.id.btnCancel)?.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.REJECT)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Azione Conferma: Validazione e attivazione del coupon
        view.findViewById<View>(R.id.btnConfirm)?.setOnClickListener {

            // 1. Controllo ridondanza: evita che l'utente attivi due volte lo stesso tipo di offerta
            if (viewModel.hasActiveCouponOfType(CouponType.BANCOFRUTTA_DISCOUNT)) {
                showToast("Coupon sconto bancofrutta già attivo")
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return@setOnClickListener
            }

            // 2. Feedback tattile per confermare l'interazione
            it.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)

            // 3. Aggiornamento stato nel ViewModel
            viewModel.markCouponActivated() // Flag legacy per compatibilità con altre componenti UI
            viewModel.activateCoupon(
                type = CouponType.BANCOFRUTTA_DISCOUNT,
                title = getString(R.string.bancofrutta_title),
                detail = null // Questo coupon non richiede una scelta specifica di prodotto
            )

            // 4. Feedback utente e chiusura del fragment
            showToast(getString(R.string.bancofrutta_activated))
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /** Helper per la visualizzazione rapida di messaggi a schermo. */
    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}