package com.example.superspan.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.QuestionAdapter
import com.example.superspan.model.Application
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda
import com.example.superspan.ui.activity.GlobalData

object ApplicationGlobal{
    val application_list = mutableListOf<Application>()

    val question_list = mutableListOf<Question>(
            Question("A", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa),
            Question("B", "", null, tipo = TipoDomanda.Aperta)
    )
}

class ApplicationFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_job_applications, container, false)
        return(view)
    }

    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listQuestion = getQuestions()
        val btnInvia = view.findViewById<ConstraintLayout>(R.id.btnInvia)
        val cbPrivacy = view.findViewById<CheckBox>(R.id.cbPrivacy)

        recyclerView = view.findViewById(R.id.rvQuestions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val controlloValidita = {
            val blankAnswer = listQuestion.any() { it.answer.isBlank() }

            if (!blankAnswer && cbPrivacy.isChecked) {
                btnInvia.isEnabled = true;
                btnInvia.alpha = 1f;
            } else {
                btnInvia.isEnabled = false;
                btnInvia.alpha = 0.3f;
                recyclerView.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.soft_red)
            }
        }

        recyclerView.adapter = QuestionAdapter(listQuestion, controlloValidita)

        // ---- Back (ID unico presente: btnBackTop) ----
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        cbPrivacy.setOnClickListener { controlloValidita() }

        btnInvia.setOnClickListener {
            val user = GlobalData.currentUser
            val currentUserId = user!!.username
            val currentOfferId = requireArguments().getString("candidaturaId") ?: "offerta_generica"

            val answers = mutableListOf<String>()

            for (domanda in listQuestion) {
                answers.add(domanda.answer)
            }

            val stringaUnica = answers.joinToString { "###" }

            val newApplication = Application(
                userId = currentUserId,
                offerId = currentOfferId,
                risposte = stringaUnica
            )

            ApplicationGlobal.application_list.add(newApplication)

            Toast.makeText(context, "Candidatura Inviata!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnInvia.isEnabled = false;
        btnInvia.alpha = 0.3f;

    }

    private fun getQuestions() : List<Question>{
        return ApplicationGlobal.question_list
    }
}