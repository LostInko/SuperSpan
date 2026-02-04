package com.example.superspan.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.ui.activity.MainActivity
import com.google.android.material.card.MaterialCardView

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Recupera l'utente corrente
        val user = GlobalData.currentUser

        // Binding dei componenti
        val cardLogout = view.findViewById<MaterialCardView>(R.id.card_logout)
        val cardDate = view.findViewById<MaterialCardView>(R.id.card_data)
        val cardJob = view.findViewById<CardView>(R.id.card_job)
        val cardAdress = view.findViewById<CardView>(R.id.card_adress)
        val cardOrder = view.findViewById<CardView>(R.id.card_order)

        // Riferimento alla TextView sopra la Carta Fedeltà (PNG)
        val tvNomeSuCarta = view.findViewById<TextView>(R.id.tv_nome_cognome_carta)

        // Impostazione testi Header
        val name = user?.name ?: "Utente"
        val surname = user?.surname ?: ""
        val fullName = "$name $surname"

        view.findViewById<TextView>(R.id.user_name).text = fullName
        view.findViewById<TextView>(R.id.user_username).text = user?.username ?: "username"

        // --- INTEGRAZIONE: Imposta il nome sopra la carta PNG ---
        tvNomeSuCarta.text = fullName.uppercase()

        // Clicco su 'i tuoi dati'
        cardDate.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserDataFragment())
                .addToBackStack(null)
                .commit()
        }

        val cardFedelta = view.findViewById<ImageView>(R.id.img_carta_fedelta)

        // Clicco sulla Carta Fedeltà

        cardFedelta.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, LoyaltyCardFragment())
                .addToBackStack(null)
                .commit()
        }

        // Clicco su 'i tuoi indirizzi'
        cardAdress.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddressListFragment())
                .addToBackStack(null)
                .commit()
        }

        // Clicco su 'i tuoi ordini'
        cardOrder.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrderHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        // Clicco su 'candidature' (ex card_job, ora nell'icona in alto a destra)
        cardJob.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ApplicationsSentFragment())
                .addToBackStack(null)
                .commit()
        }

        // Clicco su Logout
        cardLogout.setOnClickListener {
            val customView = layoutInflater.inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(customView)
                .create()

            customView.findViewById<Button>(R.id.btn_annulla).setOnClickListener {
                dialog.dismiss()
            }

            customView.findViewById<Button>(R.id.btn_conferma).setOnClickListener {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            dialog.show()
        }

        return view
    }
}