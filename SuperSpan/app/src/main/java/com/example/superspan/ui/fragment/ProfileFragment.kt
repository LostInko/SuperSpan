package com.example.superspan.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.superspan.R
import com.example.superspan.model.User
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.ui.activity.MainActivity
import com.example.superspan.ui.activity.RegisterActivity
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
        val cardJob = view.findViewById<MaterialCardView>(R.id.card_job)
        val cardAdress = view.findViewById<androidx.cardview.widget.CardView>(R.id.card_adress)
        val cardOrder = view.findViewById<androidx.cardview.widget.CardView>(R.id.card_order)

        val name = user?.name ?: "Utente"
        val surname = user?.surname ?: "Utente"
        view.findViewById<TextView>(R.id.user_name).text = "$name $surname"

        val username = user?.username ?: "Utente"
        view.findViewById<TextView>(R.id.user_username).text = "$username"


        //Clicco su 'i tuoi dati'
        cardDate.setOnClickListener {
            val fragment = UserDataFragment() // Assicurati che il nome della classe sia corretto
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Permette di tornare indietro al profilo col tasto "back"
                .commit()
        }

        //Clicco su 'i tuoi indirizzi'
        cardAdress.setOnClickListener {
            val fragment = AddressFragment()// Assicurati che il nome della classe sia corretto
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Permette di tornare indietro al profilo col tasto "back"
                .commit()
        }

        //Clicco su 'i tuoi ordini'
        cardOrder.setOnClickListener {
            val fragment = OrderHistoryFragment()// Assicurati che il nome della classe sia corretto
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Permette di tornare indietro al profilo col tasto "back"
                .commit()
        }

        //Clicco su 'le tue candidature'
        cardJob.setOnClickListener {
            val fragment = candidatureFragment()// Assicurati che il nome della classe sia corretto
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Permette di tornare indietro al profilo col tasto "back"
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

