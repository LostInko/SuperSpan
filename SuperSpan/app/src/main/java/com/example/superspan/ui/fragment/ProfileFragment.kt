package com.example.superspan.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.superspan.R
import com.example.superspan.ui.activity.MainActivity
import com.google.android.material.card.MaterialCardView
import kotlin.jvm.java

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Bind
        val cardLogout = view.findViewById<MaterialCardView>(R.id.card_logout)
        val cardDate = view.findViewById<MaterialCardView>(R.id.card_data)
        val cardOrder = view.findViewById<MaterialCardView>(R.id.card_order)


        //Clicco su Logout
        // AGGIUNGERE POP-UP DI SICUREZZA
        cardLogout.setOnClickListener {
            // 2. Torna al Login
            val intent = Intent(activity, MainActivity::class.java)
            // Queste flag puliscono lo "stack" così l'utente non può tornare indietro col tasto back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            // Chiude l'activity corrente (quella che ospita il fragment del profilo)
            activity?.finish()
        }

        return view
    }
}