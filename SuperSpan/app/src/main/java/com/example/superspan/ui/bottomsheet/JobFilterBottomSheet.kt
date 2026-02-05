package com.example.superspan.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.superspan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class JobFilterBottomSheet(
    private val currentSortMode: Int,      // 0=Nessuno, 1=Crescente, 2=Decrescente
    private val currentLocation: String,   // Il testo scritto nel filtro luogo
    private val onApply: (Int, String) -> Unit // La funzione che rimanda i dati indietro
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding
        val etLocation = view.findViewById<TextInputEditText>(R.id.etLocationFilter)
        val btnApply = view.findViewById<Button>(R.id.btnApply)

        val rbNone = view.findViewById<RadioButton>(R.id.rbNone)
        val rbAsc = view.findViewById<RadioButton>(R.id.rbAsc)
        val rbDesc = view.findViewById<RadioButton>(R.id.rbDesc)

        // 2. Ripristina lo stato precedente
        etLocation.setText(currentLocation)

        // (Se era già selezionato "Decrescente", riaccende quel pallino)
        when (currentSortMode) {
            1 -> rbAsc.isChecked = true
            2 -> rbDesc.isChecked = true
            else -> rbNone.isChecked = true
        }

        // 3. Click su "Applica Filtri"
        btnApply.setOnClickListener {
            // Controlla quale pallino è selezionato
            val selectedSort = when {
                rbAsc.isChecked -> 1
                rbDesc.isChecked -> 2
                else -> 0
            }

            // Legge il testo della città
            val selectedLocation = etLocation.text.toString().trim()

            // Manda i dati indietro al Fragment principale
            onApply(selectedSort, selectedLocation)

            // Chiude il pannello
            dismiss()
        }
    }
}