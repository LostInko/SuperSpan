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
    private var listaDomande: List<Question>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class QuestionCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.question_text)
        val tvAnswer : TextView = itemView.findViewById(R.id.tvAnswer)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title

            tvAnswer.text = domanda.answer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_check_question, parent, false)

        QuestionCheckViewHolder(view)

        return QuestionCheckViewHolder(view)
    }

    override fun getItemCount() = listaDomande.size

    fun updateList(newList: List<Question>) {
        listaDomande = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Cast del holder generico al tuo specifico QuestionCheckViewHolder
        val questionHolder = holder as QuestionCheckViewHolder

        // Recupero della domanda corretta dalla lista
        val domandaCorrente = listaDomande[position]

        // Associazione dei dati alla vista
        questionHolder.bind(domandaCorrente)
    }

}