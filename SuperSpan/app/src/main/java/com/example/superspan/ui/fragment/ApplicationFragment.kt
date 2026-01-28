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
import com.example.superspan.viewmodel.WorkWithUsViewModel
import org.w3c.dom.Text

object ApplicationGlobal{
    val application_list = mutableListOf<Application>()

    val question_list = mutableListOf<Question>(
            Question("A", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa),
            Question("B", "", null, tipo = TipoDomanda.Aperta)
    )
}

class ApplicationFragment : Fragment(){

    companion object {
        private const val ARG_ID = "-1"
        private const val ARG_NAME = "arg_name"
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_OFFER_ID = "arg_offer_id"
        private const val ARG_RISPOSTE = "arg_risposte"

        /**
         * Costruttore consigliato: passa anche l'indice se lo conosci.
         * Se non lo hai, usa -1: il fragment farà fallback per nome.
         */

        fun newInstance(
            id : Int,
            name : String,
            userId: String,
            offerId: Int,
            risposte: String

        ): ApplicationFragment {
            return ApplicationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_USER_ID, userId)
                    putInt(ARG_OFFER_ID, offerId)
                    putString(ARG_RISPOSTE, risposte)
                }
            }
        }
    }

    private lateinit var vm: WorkWithUsViewModel
    private val applicationName: String by lazy { arguments?.getString(ApplicationFragment.Companion.ARG_NAME).orEmpty() }
    private val applicationUserId: String by lazy { arguments?.getString(ApplicationFragment.Companion.ARG_USER_ID).orEmpty() }
    private val applicationOfferId: Int = arguments?.getInt(ARG_OFFER_ID) ?: -1
    private val applicationRisposte: String by lazy { arguments?.getString(ApplicationFragment.Companion.ARG_RISPOSTE).orEmpty() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_job_applications, container, false)

        view.findViewById<TextView>(R.id.offerTitle)?.text = applicationName

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
            val currentOfferId = applicationOfferId

            val answers = mutableListOf<String>()

            for (domanda in listQuestion) {
                answers.add(domanda.answer)
            }

            val stringaUnica = answers.joinToString { "###" }

            val newApplication = Application(
                name = applicationName,
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