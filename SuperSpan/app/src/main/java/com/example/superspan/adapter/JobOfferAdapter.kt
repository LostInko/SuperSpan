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

    class JobOfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val offerTitle: TextView = itemView.findViewById(R.id.offerTitle)
        val offerLocation: TextView = itemView.findViewById(R.id.offerLocation)
        val offerShift: TextView = itemView.findViewById(R.id.offerShift)
        val offerWage: TextView = itemView.findViewById(R.id.offerWage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JobOfferViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_job_offer, parent, false)

        return JobOfferViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JobOfferViewHolder,
        position: Int
    ) {
        val jobOffers = jobOfferList[position]

        holder.offerTitle.text = jobOffers.name
        holder.offerLocation.text = jobOffers.location
        holder.offerShift.text = jobOffers.shift
        holder.offerWage.text = jobOffers.wage.toString()
        holder.itemView.setOnClickListener { onItemClick(jobOffers) }
    }

    override fun getItemCount(): Int {
        return jobOfferList.size
    }
}