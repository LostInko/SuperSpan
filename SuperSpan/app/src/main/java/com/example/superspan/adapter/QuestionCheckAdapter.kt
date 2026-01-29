package com.example.superspan.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text

class QuestionCheckAdapter (
    private val listaDomande: List<Question>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class QuestionCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.question_text)
        val tvAnswer : TextView = itemView.findViewById(R.id.tvAnswer)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title

            tvAnswer.text = domanda.answer
        }
    }
    companion object {
        private const val TYPE_APERTA = 0
        private const val TYPE_MULTIPLA = 1
    }

    override fun getItemViewType(position: Int): Int {
        if (listaDomande[position].tipo == TipoDomanda.Aperta) return TYPE_APERTA
        else return TYPE_MULTIPLA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_check_question, parent, false)
        QuestionCheckViewHolder(view)

        return JobOfferSentAdapter.JobOfferSentViewHolder(view)
    }

    override fun getItemCount() = listaDomande.size

    // ViewHolder per Domanda Aperta
    inner class ApertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.question_text)
        val etAnswer : TextInputEditText = itemView.findViewById(R.id.etAnswer)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.answerBox)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title

            etAnswer.setText(domanda.answer)

        }
    }

    // ViewHolder per Domanda Multipla
    inner class MultiplaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.multiple_question_text)
        val groupAnswer : RadioGroup = itemView.findViewById(R.id.groupAnswer)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.answerBox)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title


        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val domanda = listaDomande[position]
        when (holder) {
            is ApertaViewHolder -> holder.bind(domanda)
            is MultiplaViewHolder -> holder.bind(domanda)
        }
    }

}