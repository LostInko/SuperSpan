package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment

class JobOfferFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_LOCATION = "arg_location"
        private const val ARG_SHIFT = "arg_shift"
        private const val ARG_WAGE = "arg_wage"

        /**
         * Costruttore consigliato: passa anche l'indice se lo conosci.
         * Se non lo hai, usa -1: il fragment farà fallback per nome.
         */
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

    private val jobOfferName: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_NAME).orEmpty() }
    private val jobOfferLocation: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_LOCATION).orEmpty() }
    private val jobOfferShift: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_SHIFT).orEmpty() }
    private val jobOfferWage: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_WAGE).orEmpty() }



}