package com.example.superspan.ui.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.ui.activity.MainActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class UserDataFragment : Fragment() {

    //Booleanoo per capire in che modalità siamo
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_data, container, false)

        // Binding componenti
        val backButton = view.findViewById<ImageView>(R.id.btnBackTop)
        val titleAddress = view.findViewById<TextView>(R.id.titleAddress)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etSurname = view.findViewById<TextInputEditText>(R.id.etSurname)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etCity = view.findViewById<TextInputEditText>(R.id.etCity)
        val etUser = view.findViewById<TextInputEditText>(R.id.etUser)
        val etPass = view.findViewById<TextInputEditText>(R.id.etPass)
        val btnModifica = view.findViewById<MaterialCardView>(R.id.btnModificaDati)

        // Componeneti solo nella sezione 'modifica'
        val tilConfirmPass = view.findViewById<TextInputLayout>(R.id.tilConfirmPass)
        val tvConfermaLabel = view.findViewById<TextView>(R.id.tvConfermaPassLabel)
        val tvPassHint = view.findViewById<TextView>(R.id.tvPassHint)
        val etConfirmPass = view.findViewById<TextInputEditText>(R.id.etConfirmPass)
        val tvBtnText = view.findViewById<TextView>(R.id.tvBtnText)

        // Lista di tutti gli EditText
        val editTexts = listOf(etName, etSurname, etDate, etCity, etUser, etPass)

        // Funzione di caricamento automatico dati utente
        fun loadUserData() {
            GlobalData.currentUser?.let { user ->
                etName.setText(user.name)
                etSurname.setText(user.surname)
                etDate.setText(user.date)
                etCity.setText(user.citta)
                etUser.setText(user.username)
                etPass.setText(user.password)
                etConfirmPass.setText("")
            }
        }

        // Funzione per abilitare la modifica
        fun setEditingMode(enabled: Boolean) {
            isEditing = enabled
            editTexts.forEach { it.isEnabled = enabled }
            // La data non deve essere scrivibile da tastiera, ma solo cliccabile
            etDate.isFocusable = false
            // Il campo reagisce al tocco solo se 'enabled' è true
            etDate.isClickable = enabled

            val visibility = if (enabled) View.VISIBLE else View.GONE
            tilConfirmPass.visibility = visibility
            tvConfermaLabel.visibility = visibility
            tvPassHint.visibility = visibility

            // Cambio Titolo e Testo Bottone
            titleAddress.text = if (enabled) "Modifica profilo" else "I tuoi dati"
            tvBtnText.text = if (enabled) "Conferma Modifiche" else "Modifica i tuoi dati"

        }

        // Carico dati
        loadUserData()

        // Back button
        backButton.setOnClickListener {
            // Se sta modificando, chiedi conferma
            if (isEditing) {
                val customView = layoutInflater.inflate(R.layout.dialog2, null)
                val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setView(customView)
                    .create()

                customView.findViewById<Button>(R.id.btn_annulla).setOnClickListener {
                    dialog.dismiss()
                }

                customView.findViewById<Button>(R.id.btn_conferma).setOnClickListener {
                    loadUserData() // Ripristina i dati
                    setEditingMode(false) // Esce dalla modalità modifica
                    dialog.dismiss()
                }

                dialog.show()
            } else {
                // Se non sta modificando, torna semplicemente indietro
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // Se clicco su 'Modifica'
        btnModifica.setOnClickListener {
            // Se non sono in modadiltà modifica ci entro
            if (!isEditing) {
                setEditingMode(true)
            } else {
                // Se ci sono già, significa che sto tentando di salvare le modifiche --> controlli
                val newUser = etUser.text.toString().trim()
                val newPass = etPass.text.toString()
                val confirmPass = etConfirmPass.text.toString()

                // Validazione Username
                if (newUser != GlobalData.currentUser?.username) {
                    val exists = GlobalData.user_list.any { it.username == newUser }
                    if (exists) {
                        etUser.error = "Username già esistente!"
                        return@setOnClickListener
                    }
                }

                // Validazione Password
                if (newPass.length < 8) {
                    etPass.error = "Minimo 8 caratteri"
                    return@setOnClickListener
                }
                if (newPass != confirmPass) {
                    etConfirmPass.error = "Le password non coincidono!"
                    return@setOnClickListener
                }

                // Se i controlli sono passati, salvo
                GlobalData.currentUser?.apply {
                    name = etName.text.toString()
                    surname = etSurname.text.toString()
                    date = etDate.text.toString()
                    citta = etCity.text.toString()
                    username = newUser
                    password = newPass
                }

                Toast.makeText(context, "Profilo aggiornato!", Toast.LENGTH_SHORT).show()
                setEditingMode(false)
            }

        }

        etDate.setOnClickListener {
            if (isEditing) {
                showDatePicker(etDate)
            }
        }

        return view
    }

    private fun showDatePicker(etDate: EditText) {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                // String.format garantisce il formato 01/01/2000 (aggiunge lo zero se serve)
                val selectedDate = String.format("%02d/%02d/%d", d, m + 1, y)
                etDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Imposta la data massima a oggi (non puoi essere nato nel futuro)
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }
}

