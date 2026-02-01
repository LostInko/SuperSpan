package com.example.superspan.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Document
import com.example.superspan.model.TipoFile
import com.google.android.material.card.MaterialCardView

// IL TUO ADAPTER
class DocumentsAdapter(
    private val items: MutableList<Document>,
    // Callback: Passiamo la posizione dell'elemento cliccato
    private val onAttachClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CV = 0
        private const val TYPE_VIDEO = 1
    }

    override fun getItemViewType(position: Int): Int {
        if (items[position].tipo == TipoFile.CV) return TYPE_CV
        else return TYPE_VIDEO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CV -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_cv_upload, parent, false)
                DocumentViewHolder(view)
            }
            else -> { // TYPE_VIDEO
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_video_upload, parent, false)
                VideoViewHolder(view)
            }
        }

    }

    inner class DocumentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCvName: TextView = view.findViewById(R.id.tvCvName)
        val cardUploadCv: MaterialCardView = view.findViewById(R.id.cardUploadCv)

        fun bind(item: Document, position: Int) {

            // Colore testo: nero se c'è file, grigio se default (opzionale)
            if (item.fileName != null) {
                tvCvName.text = item.fileName
                tvCvName.setTextColor(Color.BLACK)
            } else {
                tvCvName.text = "Carica il tuo CV (.pdf)"
                tvCvName.setTextColor(Color.GRAY)
            }

            // Al click, invochiamo la callback verso l'Activity
            cardUploadCv.setOnClickListener {
                onAttachClick(position)
            }
        }
    }

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVideoName: TextView = view.findViewById(R.id.tvVideoName)
        val cardUploadVideo: MaterialCardView = view.findViewById(R.id.cardUploadVideo)

        fun bind(item: Document, position: Int) {

            // Colore testo: nero se c'è video, grigio se default (opzionale)
            if (item.fileName != null) {
                tvVideoName.text = item.fileName
                tvVideoName.setTextColor(Color.BLACK)
            } else {
                tvVideoName.text = "Carica il tuo Video (vari formati supportati)"
                tvVideoName.setTextColor(Color.GRAY)
            }

            // Al click, invochiamo la callback verso l'Activity
            cardUploadVideo.setOnClickListener {
                onAttachClick(position)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DocumentViewHolder -> holder.bind(items[position], position)
            is VideoViewHolder -> holder.bind(items[position], position)
        }
    }

    override fun getItemCount() = items.size
}