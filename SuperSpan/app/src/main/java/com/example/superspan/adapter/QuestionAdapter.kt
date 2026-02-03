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

class QuestionAdapter (
    private val listaDomande: List<Question>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_APERTA = 0
        private const val TYPE_MULTIPLA = 1
    }

    override fun getItemViewType(position: Int): Int {
        if (listaDomande[position].tipo == TipoDomanda.Aperta) return TYPE_APERTA
        else return TYPE_MULTIPLA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_APERTA -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_question, parent, false)
                ApertaViewHolder(view)
            }
            else -> { // TYPE_MULTIPLA
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_multiple_choice_question, parent, false)
                MultiplaViewHolder(view)
            }
        }
    }

    override fun getItemCount() = listaDomande.size

    // ViewHolder per Domanda Aperta
    inner class ApertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.question_text)
        val etAnswer : TextInputEditText = itemView.findViewById(R.id.etAnswer)
        var currentTextWatcher : TextWatcher? = null
        val cardView = itemView.findViewById<MaterialCardView>(R.id.answerBox)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title

            currentTextWatcher?.let { etAnswer.removeTextChangedListener(it) }

            etAnswer.setText(domanda.answer)


            updateErrorState(domanda.answer.isBlank())

            currentTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
                override fun afterTextChanged(s: Editable?) {
                    val testo = s.toString()
                    domanda.answer = testo

                    /*if(domanda.hasError) {
                        domanda.hasError = false
                        updateErrorState(domanda)
                    }*/

                    updateErrorState(testo.isBlank())

                    onDataChanged()
                }
            }

            etAnswer.addTextChangedListener(currentTextWatcher)
        }

        private fun updateErrorState(isEmpty : Boolean) {
            if (isEmpty) {
                cardView.strokeColor = Color.parseColor("#4DFF0000")
                cardView.strokeWidth = 4
            } else {
                cardView.strokeColor = Color.parseColor("#BDBDBD")
                cardView.strokeWidth = 2
            }
        }
    }

    // ViewHolder per Domanda Multipla
    inner class MultiplaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDomanda : TextView = itemView.findViewById(R.id.multiple_question_text)
        val groupAnswer : RadioGroup = itemView.findViewById(R.id.groupAnswer)
        val cardView = itemView.findViewById<MaterialCardView>(R.id.answerBox)

        fun bind(domanda : Question){
            tvDomanda.text = domanda.title

            groupAnswer.setOnCheckedChangeListener(null)
            groupAnswer.removeAllViews()

            updateErrorState(domanda.answer.isBlank())

            domanda.options?.forEach { opzione ->
                val radioButton = RadioButton(itemView.context).apply {
                    text = opzione
                    id = View.generateViewId()

                    textSize = 14f

                    setTextColor(androidx.core.content.ContextCompat.getColorStateList(context, R.color.desc_color))

                    val colorStateList = ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked), // Non selezionato
                            intArrayOf(android.R.attr.state_checked)    // Selezionato
                        ),
                        intArrayOf(
                            R.color.red,   // Colore cerchio vuoto
                            R.color.blue // Colore cerchio pieno
                        )
                    )
                    androidx.core.widget.CompoundButtonCompat.setButtonTintList(this, colorStateList)

                    setPadding(16, 0, 0, 0)

                    val params = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(0, 8, 0, 8) // Spazio sopra e sotto (in pixel approssimati)
                    layoutParams = params

                }
                groupAnswer.addView(radioButton)

                if(domanda.answer == opzione){
                    radioButton.isChecked = true
                }

            }

            groupAnswer.setOnCheckedChangeListener { group, checkedId ->
                val selectedButton = group.findViewById<RadioButton>(checkedId)

                selectedButton?.let {
                    val testo = it.text.toString()
                    domanda.answer = testo
                    domanda.hasError = false
                    updateErrorState(testo.isBlank())
                    onDataChanged()
                }
            }

        }

        private fun updateErrorState(isEmpty : Boolean) {
            if (isEmpty) {
                cardView.strokeColor = Color.parseColor("#4DFF0000")
                cardView.strokeWidth = 4
            } else {
                cardView.strokeColor = Color.parseColor("#BDBDBD")
                cardView.strokeWidth = 2
            }
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