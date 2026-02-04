package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.model.JobOffer
import com.example.superspan.model.Question
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.viewmodel.WorkWithUsViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class JobOfferFragment : Fragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_LOCATION = "arg_location"
        private const val ARG_SHIFT = "arg_shift"
        private const val ARG_WAGE = "arg_wage"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_REQ = "arg_req"
        private const val ARG_IMG = "arg_img"

        fun newInstance(
            id : Int,
            name: String,
            location: String,
            shift: String,
            wage: Double,
            desc: String,
            req: String,
            image: Int
        ): JobOfferFragment {
            return JobOfferFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_LOCATION, location)
                    putString(ARG_SHIFT, shift)
                    putDouble(ARG_WAGE, wage)
                    putString(ARG_DESC, desc)
                    putString(ARG_REQ, req)
                    putInt(ARG_IMG, image)
                }
            }
        }
    }

    private lateinit var vm: WorkWithUsViewModel

    private val jobOfferId : Int by lazy { arguments?.getInt(ARG_ID) ?: -1 }
    private val jobOfferName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val jobOfferLocation: String by lazy { arguments?.getString(ARG_LOCATION).orEmpty() }
    private val jobOfferShift: String by lazy { arguments?.getString(ARG_SHIFT).orEmpty() }
    private val jobOfferWage: Double by lazy { arguments?.getDouble(ARG_WAGE) ?: -2.0 }
    private val jobOfferDesc: String by lazy { arguments?.getString(ARG_DESC).orEmpty() }
    private val jobOfferReq: String by lazy { arguments?.getString(ARG_REQ).orEmpty() }
    private val jobOfferImage : Int by lazy { arguments?.getInt(ARG_IMG) ?: -1 }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_job_offer, container, false)

        // Collegamento delle View
        v.findViewById<TextView>(R.id.offer_title)?.text = jobOfferName
        v.findViewById<Chip>(R.id.offer_location)?.text = jobOfferLocation
        v.findViewById<Chip>(R.id.offer_shift)?.text = jobOfferShift
        v.findViewById<Chip>(R.id.offer_wage)?.text = jobOfferWage.toString()
        v.findViewById<TextView>(R.id.offer_description)?.text = jobOfferDesc
        v.findViewById<TextView>(R.id.offer_requisiti)?.text = jobOfferReq
        v.findViewById<ImageView>(R.id.topImage).setImageResource(jobOfferImage)

        // Back Button
        v.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val btnCandidati = v.findViewById<ConstraintLayout>(R.id.btn_candidati)
        val currentUserId = GlobalData.currentUser!!.username

        // Bottone Candidati, rimanda alla pagina che permette di compilare la domanda
        btnCandidati.setOnClickListener {

            val myApplicationIds = ApplicationGlobal.application_list
                .filter { it.userId == currentUserId } // Filtra per utente corretto
                .map { it.offerId } // Prende solo gli ID

            if(myApplicationIds.contains(jobOfferId)){
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("Attenzione! Hai già inviato una candidatura per questa posizione!")
                dialog.setPositiveButton("Ok", null)
                dialog.create()

                Toast.makeText(context, "Hai già inviato una candidatura per questa posizione!", Toast.LENGTH_SHORT).show()
            } else {
                val fragmentDomande = ApplicationFragment.newInstance(
                    userId = GlobalData.currentUser!!.username,
                    name = jobOfferName,
                    offerId = jobOfferId,
                    risposte = ""
                )

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentDomande)
                    .addToBackStack(null) // Permette all'utente di tornare indietro col tasto back
                    .commit()
            }
        }

        return v
    }

}