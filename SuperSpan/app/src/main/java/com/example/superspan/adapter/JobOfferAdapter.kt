package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.model.JobOffer
import com.example.superspan.R

class JobOfferAdapter(
    private val jobOfferList: List<JobOffer>,
    private val onItemClick: (JobOffer) -> Unit
) : RecyclerView.Adapter<JobOfferAdapter.JobOfferViewHolder>() {

    class JobOfferViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtTitle: TextView =
            itemView.findViewById(R.id.txtTitle)
        val txtLocation: TextView =
            itemView.findViewById(R.id.txtLocation)
        val txtShift: TextView =
            itemView.findViewById(R.id.txtShift)
        val txtWage: TextView =
            itemView.findViewById(R.id.txtWage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JobOfferViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_job_offer, parent, false)

        return JobOfferViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JobOfferViewHolder,
        position: Int
    ) {
        val jobOffers = jobOfferList[position]

        holder.txtTitle.text = jobOffers.name
        holder.txtLocation.text = jobOffers.location
        holder.txtShift.text = jobOffers.shift
        holder.txtWage.text = jobOffers.wage
        holder.itemView.setOnClickListener { onItemClick(jobOffers) }
    }

    override fun getItemCount(): Int {
        return jobOfferList.size
    }
}