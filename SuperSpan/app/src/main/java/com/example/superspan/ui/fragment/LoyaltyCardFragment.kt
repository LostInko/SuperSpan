package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.google.android.material.button.MaterialButton

class LoyaltyCardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate semplice, la logica va sotto
        return inflater.inflate(R.layout.fragment_loyalty_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Riferimenti alle View
        val tvPoints = view.findViewById<TextView>(R.id.tvPointsDetail)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val btnBack = view.findViewById<MaterialButton>(R.id.btnBack)

        // Recupero dati dall'utente globale
        val user = GlobalData.currentUser

        // Impostiamo il nome e i punti (se presenti)
        tvUserName.text = user?.name ?: "Cliente SuperSpan"
        // Se vuoi rendere dinamici anche i punti:

        // Torna indietro al profilo
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}