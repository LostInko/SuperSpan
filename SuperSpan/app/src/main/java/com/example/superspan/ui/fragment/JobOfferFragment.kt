package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.model.Question
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.viewmodel.WorkWithUsViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class JobOfferFragment : Fragment() {

    companion object {
        private const val ARG_ID = "-1"
        private const val ARG_NAME = "arg_name"
        private const val ARG_LOCATION = "arg_location"
        private const val ARG_SHIFT = "arg_shift"
        private const val ARG_WAGE = "arg_wage"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_REQ = "arg_req"

        /**
         * Costruttore consigliato: passa anche l'indice se lo conosci.
         * Se non lo hai, usa -1: il fragment farà fallback per nome.
         */

        fun newInstance(
            id : Int,
            name: String,
            location: String,
            shift: String,
            wage: String,
            desc: String,
            req: String
        ): JobOfferFragment {
            return JobOfferFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_LOCATION, location)
                    putString(ARG_SHIFT, shift)
                    putString(ARG_WAGE, wage)
                    putString(ARG_DESC, desc)
                    putString(ARG_REQ, req)
                }
            }
        }
    }

    private lateinit var vm: WorkWithUsViewModel

    private val jobOfferName: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_NAME).orEmpty() }
    private val jobOfferLocation: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_LOCATION).orEmpty() }
    private val jobOfferShift: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_SHIFT).orEmpty() }
    private val jobOfferWage: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_WAGE).orEmpty() }
    private val jobOfferDesc: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_DESC).orEmpty() }
    private val jobOfferReq: String by lazy { arguments?.getString(JobOfferFragment.Companion.ARG_REQ).orEmpty() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_job_offer, container, false)

        // ---- Bind dati statici (coerenti con l'XML) ----
        v.findViewById<TextView>(R.id.offer_title)?.text = jobOfferName
        v.findViewById<Chip>(R.id.offer_location)?.text = jobOfferLocation
        v.findViewById<Chip>(R.id.offer_shift)?.text = jobOfferShift
        v.findViewById<Chip>(R.id.offer_wage)?.text = jobOfferWage
        v.findViewById<TextView>(R.id.offer_description)?.text = jobOfferDesc
        v.findViewById<TextView>(R.id.offer_requisiti)?.text = jobOfferReq

        // ---- Back (ID unico presente: btnBackTop) ----
        v.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val btnCandidati = v.findViewById<ConstraintLayout>(R.id.btn_candidati)

        btnCandidati.setOnClickListener {
            val fragmentDomande = ApplicationFragment().apply {
                arguments = Bundle().apply {
                    putString("candidaturaId", jobOfferName)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentDomande) // R.id.fragment_container è l'ID nel tuo layout activity
                .addToBackStack(null) // Permette all'utente di tornare indietro col tasto back
                .commit()
        }


        return v
    }

}