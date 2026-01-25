package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.JobOfferAdapter
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.JobOffer
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.viewmodel.WorkWithUsViewModel
import kotlin.collections.indexOfFirst
import kotlin.collections.orEmpty

class WorkWithUsFragment : Fragment() {
    private lateinit var vm: WorkWithUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_work_with_us, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]

        val recyclerJobOffers = view.findViewById<RecyclerView>(R.id.recyclerJobOffers)
        recyclerJobOffers.layoutManager = GridLayoutManager(context, 1)

        val etSearch = view.findViewById<EditText>(R.id.search_bar)

        vm.jobOffers.observe(viewLifecycleOwner) { jobOfferList ->

            fun updateList(query: String?) {
                val filteredList = if (query.isNullOrEmpty()) {
                    jobOfferList // Se vuoto, mostra tutto
                } else {
                    jobOfferList.filter { it.name.contains(query, ignoreCase = true) }
                }

                recyclerJobOffers.adapter = JobOfferAdapter(
                    jobOfferList = filteredList,

                    onItemClick = { jobOffer ->
                        val fullList = vm.jobOffers.value.orEmpty()
                        val index = fullList.indexOfFirst { it.id == jobOffer.id }

                        val fragment = JobOfferFragment.newInstance(
                            id = jobOffer.id,
                            name = jobOffer.name,
                            location = jobOffer.location,
                            shift = jobOffer.shift,
                            wage = jobOffer.wage,
                            desc = jobOffer.description,
                            req = jobOffer.requirements
                        )
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
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
}