package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.JobOfferAdapter
import com.example.superspan.adapter.ProductAdapter
import com.example.superspan.model.JobOffer

class WorkWithUsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_work_with_us, container, false)

        val recyclerJobOffers = view.findViewById<RecyclerView>(R.id.recyclerJobOffers)
        recyclerJobOffers.layoutManager = GridLayoutManager(context, 1)

        val jobOffers = listOf(
            JobOffer("Magazziniere", "Teramo", "Turno notte", "€€")
        )

        recyclerJobOffers.adapter = JobOfferAdapter(jobOffers) { jobOffer ->
            val fragment = JobOfferFragment.newInstance(
                name = jobOffer.name,
                location = jobOffer.location,
                shift = jobOffer.shift,
                wage = jobOffer.wage
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}