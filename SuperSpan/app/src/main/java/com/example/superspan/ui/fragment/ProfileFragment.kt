package com.example.superspan.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.superspan.R
import com.example.superspan.ui.activity.MainActivity
import com.google.android.material.card.MaterialCardView

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Binding dei componenti
        val cardLogout = view.findViewById<MaterialCardView>(R.id.card_logout)
        val cardDate = view.findViewById<MaterialCardView>(R.id.card_data)
        val cardOrder = view.findViewById<MaterialCardView>(R.id.card_order)

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
                // Logica logout qui
                // 2. Torna al Login
                val intent = Intent(requireActivity(), MainActivity::class.java)

                // Pulisce lo stack: l'utente non può tornare indietro
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // Chiude l'activity corrente
                requireActivity().finish()
            }

            dialog.show()
        }

        return view
    }
}

