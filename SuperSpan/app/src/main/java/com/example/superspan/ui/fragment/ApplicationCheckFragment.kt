package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.QuestionAdapter
import com.example.superspan.adapter.QuestionCheckAdapter
import com.example.superspan.model.Application
import com.example.superspan.model.JobOffer
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.viewmodel.HomeViewModel
import com.example.superspan.viewmodel.WorkWithUsViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class ApplicationCheckFragment : Fragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_LOCATION = "arg_location"
        private const val ARG_SHIFT = "arg_shift"
        private const val ARG_WAGE = "arg_wage"
        private const val ARG_RISP = "arg_risp"
        private const val ARG_FILES = "arg_files"


        fun newInstance(
            id : Int,
            name: String,
            location: String,
            shift: String,
            wage: Double,
            risp: String,
            files : String
        ): ApplicationCheckFragment {
            return ApplicationCheckFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_LOCATION, location)
                    putString(ARG_SHIFT, shift)
                    putDouble(ARG_WAGE, wage)
                    putString(ARG_RISP, risp)
                    putString(ARG_FILES, files)
                }
            }
        }
    }

    private lateinit var vm: WorkWithUsViewModel

    private val jobOfferId : Int by lazy { arguments?.getInt(ARG_ID) ?: -1 }
    private val jobOfferName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val jobOfferLocation: String by lazy { arguments?.getString(ARG_LOCATION).orEmpty() }
    private val jobOfferShift: String by lazy { arguments?.getString(ARG_SHIFT).orEmpty() }
    private val jobOfferWage: Double by lazy { arguments?.getDouble(ARG_WAGE) ?: -2.0 }
    private val appicationRisp: String by lazy { arguments?.getString(ARG_RISP).orEmpty() }
    private val appicationFiles: String by lazy { arguments?.getString(ARG_FILES).orEmpty() }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[WorkWithUsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_application_sent_check, container, false)

        // Collegamento delle View
        v.findViewById<TextView>(R.id.offer_title)?.text = jobOfferName
        v.findViewById<Chip>(R.id.offer_location)?.text = jobOfferLocation
        v.findViewById<Chip>(R.id.offer_shift)?.text = jobOfferShift
        v.findViewById<Chip>(R.id.offer_wage)?.text = jobOfferWage.toString()

        // Back button
        v.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        var rvQuestion : RecyclerView = v.findViewById(R.id.rvQuestions)
        rvQuestion.layoutManager = LinearLayoutManager(requireContext())

        val listQuestion = ApplicationGlobal.question_list
        val answers = appicationRisp.split("###")

        listQuestion.forEachIndexed { index, question ->
            // Verifichiamo che esista una domanda per questo indice per evitare crash
            if (index < answers.size) {
                question.answer = answers[index]
            }
        }

        val listFiles = ApplicationGlobal.docs_list
        val files = appicationFiles.split("&&&")
        listFiles.forEachIndexed { index, file ->
            // Verifichiamo che esista un file per questo indice per evitare crash
            if (index < files.size) {
                file.fileName = files[index]
            }
            val newQuestion = Question(title = file.fileTitle, answer = file.fileName, options = listOf(), tipo = TipoDomanda.Aperta, hasError = false )
            listQuestion.add(newQuestion)
        }


        rvQuestion.adapter = QuestionCheckAdapter(listQuestion)

        return v
    }

}