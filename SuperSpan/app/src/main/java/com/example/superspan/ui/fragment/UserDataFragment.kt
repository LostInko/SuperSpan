package com.example.superspan.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class UserDataFragment : Fragment() {

    private var isEditing = false
    private var originalPassword = "" // Memorizza la password attuale dell'utente

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

        // Componenti modifica
        val tilConfirmPass = view.findViewById<TextInputLayout>(R.id.tilConfirmPass)
        val tvConfermaLabel = view.findViewById<TextView>(R.id.tvConfermaPassLabel)
        val tvPassHint = view.findViewById<TextView>(R.id.tvPassHint)
        val etConfirmPass = view.findViewById<TextInputEditText>(R.id.etConfirmPass)
        val tvBtnText = view.findViewById<TextView>(R.id.tvBtnText)
        val tilUser = view.findViewById<TextInputLayout>(R.id.tilUser)
        val tilPass = view.findViewById<TextInputLayout>(R.id.tilPass)

        val editTexts = listOf(etName, etSurname, etDate, etCity, etUser, etPass)

        // Caricamento dati
        fun loadUserData() {
            GlobalData.currentUser?.let { user ->
                etName.setText(user.name)
                etSurname.setText(user.surname)
                etDate.setText(user.date)
                etCity.setText(user.citta)
                etUser.setText(user.username)
                etPass.setText(user.password)

                originalPassword = user.password // Salviamo la password originale per il confronto
                etConfirmPass.setText("")
            }
            tilUser.error = null
            tilPass.error = null
            tilConfirmPass.error = null
        }

        //Abilitare/Disabilitare tasto conferma
        fun updateBtnEnableState() {
            if (isEditing) {
                val modificato = haModificatoQualcosa(editTexts)
                btnModifica.isEnabled = modificato
                btnModifica.alpha = if (modificato) 1.0f else 0.6f
            } else {
                btnModifica.isEnabled = true
                btnModifica.alpha = 1.0f
            }
        }

        // Gestione Modalità Modifica
        fun setEditingMode(enabled: Boolean) {
            isEditing = enabled
            editTexts.forEach { it.isEnabled = enabled }
            etDate.isFocusable = false
            etDate.isClickable = enabled


            tilConfirmPass.visibility = View.GONE
            tvConfermaLabel.visibility = View.GONE
            tvPassHint.visibility = View.GONE

            titleAddress.text = if (enabled) "Modifica profilo" else "I tuoi dati"
            tvBtnText.text = if (enabled) "Conferma Modifiche" else "Modifica i tuoi dati"

            updateBtnEnableState()
        }

        // Mostra conferma solo se la password cambia
        etPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()

                // Mostriamo i campi di conferma solo se siamo in modalità editing
                // E se il testo è diverso da quello salvato nel profilo
                if (isEditing && currentText != originalPassword) {
                    tilConfirmPass.visibility = View.VISIBLE
                    tvConfermaLabel.visibility = View.VISIBLE
                    tvPassHint.visibility = View.VISIBLE
                } else {
                        // Se torniamo alla pass originale, puliamo tutto
                        resetFieldError(tilPass, etPass)
                        resetFieldError(tilConfirmPass, etConfirmPass)

                        tilConfirmPass.visibility = View.GONE
                        tvConfermaLabel.visibility = View.GONE
                        tvPassHint.visibility = View.GONE
                        etConfirmPass.setText("")

                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadUserData()

        // Listener Bottone Modifica/Conferma
        btnModifica.setOnClickListener {
            if (!isEditing) {
                setEditingMode(true)
            } else {
                val newUser = etUser.text.toString().trim()
                val newPass = etPass.text.toString()
                val confirmPass = etConfirmPass.text.toString()
                var isValid = true

                // Controllo Username
                if (newUser != GlobalData.currentUser?.username) {
                    if (GlobalData.user_list.any { it.username == newUser }) {
                        etUser.error = "Username già esistente!"
                        etUser.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
                        isValid = false
                    }
                }

                // Controllo Password (solo se è stata cambiata)
                if (newPass != originalPassword) {
                    if (newPass.length < 8) {
                        etPass.error = "Minimo 8 caratteri"
                        etPass.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
                        isValid = false
                    }
                    if (newPass != confirmPass) {
                        etConfirmPass.error = "Le password non coincidono!"
                        etConfirmPass.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
                        isValid = false
                    }
                }

                if (isValid) {
                    GlobalData.currentUser?.apply {
                        name = etName.text.toString()
                        surname = etSurname.text.toString()
                        date = etDate.text.toString()
                        citta = etCity.text.toString()
                        username = newUser
                        password = newPass
                    }
                    Toast.makeText(context, "Profilo aggiornato!", Toast.LENGTH_SHORT).show()
                    loadUserData() // Aggiorna la password originale salvata
                    setEditingMode(false)
                }
            }
        }

        // Altri listener (Back button, DatePicker, etc.)
        backButton.setOnClickListener {
            if (isEditing) {
                // Dialog di conferma uscita (come nel tuo codice originale)
                showExitDialog {
                    loadUserData()
                    setEditingMode(false)
                }
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        val campiDaAscoltare = listOf(etName, etSurname, etCity)
        campiDaAscoltare.forEach { et ->
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateBtnEnableState()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        fun setupClearErrorOnType(til: TextInputLayout, et: TextInputEditText) {
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    resetFieldError(til, et)
                    updateBtnEnableState()
                }
                override fun afterTextChanged(s: Editable?) {}

            })

        }

        setupClearErrorOnType(tilUser, etUser)
        setupClearErrorOnType(tilPass, etPass)
        setupClearErrorOnType(tilConfirmPass, etConfirmPass)


        fun showDatePickerInterno(etDate: EditText) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                etDate.setText(String.format("%02d/%02d/%d", d, m + 1, y))

                updateBtnEnableState()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etDate.setOnClickListener { if (isEditing) showDatePickerInterno(etDate) }


        return view
    }

    private fun haModificatoQualcosa(editTexts: List<TextInputEditText>): Boolean {
        val u = GlobalData.currentUser ?: return false
        return editTexts[0].text.toString() != u.name ||
                editTexts[1].text.toString() != u.surname ||
                editTexts[2].text.toString() != u.date ||
                editTexts[3].text.toString() != u.citta ||
                editTexts[4].text.toString() != u.username ||
                editTexts[5].text.toString() != u.password
    }

    private fun showExitDialog(onConfirm: () -> Unit) {
        val customView = layoutInflater.inflate(R.layout.dialog2, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(customView).create()
        customView.findViewById<Button>(R.id.btn_annulla).setOnClickListener { dialog.dismiss() }
        customView.findViewById<Button>(R.id.btn_conferma).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        dialog.show()
    }
    fun resetFieldError(til: TextInputLayout, et: TextInputEditText) {
        // 1. Rimuoviamo il testo dell'errore
        til.error = null

        // 2. Nascondiamo forzatamente l'icona del punto esclamativo
        til.errorIconDrawable = null

        // 3. Disabilitiamo il meccanismo di errore
        til.isErrorEnabled = false

        // 4. Ripristiniamo il colore dello sfondo dell'EditText
        et.backgroundTintList = null
    }
}