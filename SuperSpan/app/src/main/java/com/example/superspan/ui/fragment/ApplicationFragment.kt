package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.QuestionAdapter
import com.example.superspan.model.Question
import com.example.superspan.model.TipoDomanda

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

        recyclerView = view.findViewById(R.id.rvQuestions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = QuestionAdapter(listQuestion)


        // ---- Back (ID unico presente: btnBackTop) ----
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun getQuestions() : List<Question>{
        return listOf(
            Question("A", "", listOf("a", "b"), tipo = TipoDomanda.Chiusa),
            Question("B", "", null, tipo = TipoDomanda.Aperta)
        )
    }
}