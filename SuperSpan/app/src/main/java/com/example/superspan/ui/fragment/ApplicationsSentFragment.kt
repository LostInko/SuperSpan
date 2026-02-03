package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.JobOfferAdapter
import com.example.superspan.adapter.JobOfferSentAdapter
import com.example.superspan.model.JobOffer
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.viewmodel.WorkWithUsViewModel

class ApplicationsSentFragment : Fragment() {
    private lateinit var vm: WorkWithUsViewModel
    private lateinit var recyclerJobOffers: RecyclerView
    private var myAppliedOffers: List<JobOffer> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_job_applications_sent, container, false)

        // ---- Back (ID unico presente: btnBackTop) ----
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]

        recyclerJobOffers = view.findViewById(R.id.recyclerJobOffersSent)
        recyclerJobOffers.layoutManager = GridLayoutManager(context, 1)

        val etSearch = view.findViewById<EditText>(R.id.search_bar)

        val emptyView : TextView = view.findViewById(R.id.tvEmpty)

        val currentUserId = GlobalData.currentUser!!.username
        myAppliedOffers = vm.getAppliedOffersForUser(currentUserId)

        // 3. LOGICA DI FILTRO DIRETTA (DEBUG MODE)
        // Recuperiamo tutte le offerte dal ViewModel
        val allOffers = vm.jobOffers.value ?: emptyList()

        val myApplicationIds = ApplicationGlobal.application_list
            .filter { it.userId == currentUserId } // Filtra per utente corretto
            .map { it.offerId } // Prende solo gli ID (che sono Int)

        // Incrociamo i dati: Tieni l'offerta SE il suo ID è nelle mie candidature
        myAppliedOffers = allOffers.filter { offer ->
            myApplicationIds.contains(offer.id)
        }


        fun updateList(query: String?) {

            val offersToShow = if (query.isNullOrEmpty()) {
                myAppliedOffers
            } else {
                myAppliedOffers.filter { it.name.contains(query, ignoreCase = true) }
            }
            
            emptyView.visibility = if(offersToShow.isNullOrEmpty()) View.VISIBLE else View.GONE

            recyclerJobOffers.adapter = JobOfferSentAdapter(
                jobOfferList = offersToShow,

                onItemClick = { jobOffer ->
                    for (application in ApplicationGlobal.application_list){
                        if (application.offerId == jobOffer.id){
                            val fragment = ApplicationCheckFragment.newInstance(
                                id = jobOffer.id,
                                name = jobOffer.name,
                                location = jobOffer.location,
                                shift = jobOffer.shift,
                                wage = jobOffer.wage,
                                desc = jobOffer.description,
                                risp = application.risposte,
                                files = application.files,
                                image = jobOffer.image
                            )
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                },
            )
        }

        // Caricamento iniziale
        updateList("")

        // Ascolta i cambiamenti di testo
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateList(s.toString()) // Filtra ogni volta che l'utente scrive
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }
}