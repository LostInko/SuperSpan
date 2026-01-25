package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.google.android.material.textfield.TextInputEditText

class UserDataFragment : Fragment() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_data, container, false)

        // Binding dei componenti
        val backButton = view.findViewById<ImageView>(R.id.btnBackTop)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etSurname = view.findViewById<TextInputEditText>(R.id.etSurname)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etCity = view.findViewById<TextInputEditText>(R.id.etCity)
        val etUser = view.findViewById<TextInputEditText>(R.id.etUser)
        val etPass = view.findViewById<TextInputEditText>(R.id.etPass)

        // Click sul pulsante indietro
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //Recupero dell'utente corrente
        val currentUser = GlobalData.currentUser

        // 3. Popolamento automatico (se l'utente esiste)
        currentUser?.let { user ->
            etName.setText(user.name)
            etSurname.setText(user.surname)
            etDate.setText(user.date)
            etCity.setText(user.citta)
            etUser.setText(user.username)
            etPass.setText(user.password)
        }

        return view
    }
}