package com.example.superspan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Document
import com.google.android.material.card.MaterialCardView

// IL TUO ADAPTER
class DocumentsAdapter(
    private val items: List<Document>,
    // Callback: Passiamo la posizione dell'elemento cliccato
    private val onAttachClick: (Int) -> Unit
) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCvName: TextView = view.findViewById(R.id.tvCvName)
        val cardUploadCv: MaterialCardView = view.findViewById(R.id.cardUploadCv)

        fun bind(item: Document, position: Int) {
            // Se abbiamo già un file, mostriamo il nome, altrimenti il testo di default
            tvCvName.text = item.fileName ?: "Carica il tuo CV (.pdf)"

            // Colore testo: nero se c'è file, grigio se default (opzionale)
            if (item.fileName != null) {
                tvCvName.setTextColor(Color.BLACK)
            }

            // Al click, invochiamo la callback verso l'Activity
            cardUploadCv.setOnClickListener {
                onAttachClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_cv_upload, parent, false) // Il layout che mi hai mandato
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size
}