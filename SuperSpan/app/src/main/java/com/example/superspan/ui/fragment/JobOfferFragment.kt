package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

class JobOfferFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_LOCATION = "arg_location"
        private const val ARG_SHIFT = "arg_shift"
        private const val ARG_WAGE = "arg_wage"

        // Creo il fragment con i dati del prodotto
        fun newInstance(
            name: String,
            location: String,
            shift: String,
            wage: String
        ): JobOfferFragment {
            return JobOfferFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_LOCATION, location)
                    putString(ARG_SHIFT, shift)
                    putString(ARG_WAGE, wage)
                }
            }
        }
    }

}