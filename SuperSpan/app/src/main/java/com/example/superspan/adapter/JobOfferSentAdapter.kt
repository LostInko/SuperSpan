package com.example.superspan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.model.JobOffer
import com.example.superspan.R

class JobOfferSentAdapter(
    private val jobOfferList: List<JobOffer>,
    private val onItemClick: (JobOffer) -> Unit
) : RecyclerView.Adapter<JobOfferSentAdapter.JobOfferSentViewHolder>() {

    class JobOfferSentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val offerTitle: TextView = itemView.findViewById(R.id.offerTitle)
        val offerLocation: TextView = itemView.findViewById(R.id.offerLocation)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JobOfferSentViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_job_offer_sent, parent, false)

        return JobOfferSentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: JobOfferSentViewHolder,
        position: Int
    ) {
        val jobOffers = jobOfferList[position]

        holder.offerTitle.text = jobOffers.name
        holder.offerLocation.text = jobOffers.location
        holder.itemView.setOnClickListener { onItemClick(jobOffers) }
    }

    override fun getItemCount(): Int {
        return jobOfferList.size
    }
}