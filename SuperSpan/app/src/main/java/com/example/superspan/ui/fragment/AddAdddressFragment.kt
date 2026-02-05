package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.superspan.R
import com.example.superspan.model.Address
import com.example.superspan.viewmodel.HomeViewModel
import com.google.android.material.textfield.TextInputLayout

class AddAddressFragment : Fragment(R.layout.fragment_add_address) {

    private lateinit var vm: HomeViewModel

    // Serve per fare le mappe, è personale dell'account GULUGULU di Diego
    private val API_KEY = "AIzaSyBhUqEjaJ14r8GvdmfYVkKtpVhXpoY3dYI"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val etStreet = view.findViewById<EditText>(R.id.etAddressStreet)
        val etCity = view.findViewById<EditText>(R.id.etAddressCity)
        val imgPreview = view.findViewById<ImageView>(R.id.imgMapPreviewAdd)
        val btnSave = view.findViewById<Button>(R.id.btnSaveAddress)
        val etNameAddress = view.findViewById<EditText>(R.id.etAddressName)
        val etCap = view.findViewById<EditText>(R.id.etAddressCap)

        val backButton = view.findViewById<AppCompatImageView>(R.id.btnBack)

        val tilName = view.findViewById<TextInputLayout>(R.id.tilAddressName)
        val tilCity = view.findViewById<TextInputLayout>(R.id.tilAddressCity)
        val tilStreet = view.findViewById<TextInputLayout>(R.id.tilAddressStreet)
        val tilCap = view.findViewById<TextInputLayout>(R.id.tilAddressCap)

        // Funzione per caricare la mappa
        fun loadMap() {
            val address = etStreet.text.toString().trim()
            val city = etCity.text.toString().trim()

            if (address.isNotEmpty() && city.isNotEmpty()) {
                val query = "$address, $city".replace(" ", "+")
                val url = "https://maps.googleapis.com/maps/api/staticmap?" +
                        "center=$query" +
                        "&zoom=15" +
                        "&size=600x300" +
                        "&markers=color:red|$query" + // Aggiunge il pin rosso sulla posizione
                        "&key=$API_KEY"

                Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(imgPreview)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                loadMap()
                //Rimuovi gli errori quando l'utente scrive
                tilName.error = null
                tilCity.error = null
                tilStreet.error = null
                tilCap.error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etStreet.addTextChangedListener(textWatcher)
        etCity.addTextChangedListener(textWatcher)
        etNameAddress.addTextChangedListener(textWatcher)
        etCap.addTextChangedListener(textWatcher)
        btnSave.isEnabled = false
        btnSave.alpha = 0.6f

        // (Attiva/Disattiva bottone)
        fun checkValidation() {
            val isFilled = etNameAddress.text.isNotBlank() &&
                    etCity.text.isNotBlank() &&
                    etStreet.text.isNotBlank() &&
                    etCap.text.isNotBlank()

            if (isFilled) {
                btnSave.isEnabled = true
                btnSave.alpha = 1f
            } else {
                btnSave.isEnabled = false
                btnSave.alpha = 0.6f
            }
        }

        //Funzione per caricare la mappa
        fun updateMap() {
            val street = etStreet.text.toString().trim()
            val city = etCity.text.toString().trim()

            if (street.isNotEmpty() && city.isNotEmpty()) {
                val query = "$street, $city".replace(" ", "+")
                val url = "https://maps.googleapis.com/maps/api/staticmap?" +
                        "center=$query&zoom=16&size=600x300" +
                        "&markers=color:red|$query&key=$API_KEY"

                Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_mapmode)
                    .into(imgPreview)
            }
        }

        fun setupWatcher(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // A. Togli la tinta rossa e l'errore appena l'utente scrive
                    editText.backgroundTintList = null
                    editText.error = null

                    // B. Aggiorna la mappa (solo se sono i campi via o città)
                    if (editText == etStreet || editText == etCity) {
                        updateMap()
                    }

                    // C. Controlla se abilitare il bottone
                    checkValidation()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        // Applichiamo il watcher a tutti i campi
        setupWatcher(etNameAddress)
        setupWatcher(etCity)
        setupWatcher(etStreet)
        setupWatcher(etCap)

        // Click su Salva con controlli extra (es. lunghezza CAP) ---
        btnSave.setOnClickListener {
            var invalid = false
            val capText = etCap.text.toString().trim()

            // Controllo specifico sul CAP (5 cifre)
            if (capText.length != 5) {
                etCap.error = "Il CAP deve avere 5 cifre"
                etCap.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
                invalid = true
            }

            if (!invalid) {
                val newAddress = Address(
                    City = etCity.text.toString().trim(),
                    Address = etStreet.text.toString().trim(),
                    CAP = capText,
                    Name = etNameAddress.text.toString().trim(),
                    isSelected = true
                )
                vm.addAddress(newAddress)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

}