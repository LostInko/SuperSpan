package com.example.superspan.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.superspan.R
import com.example.superspan.ui.activity.HomeActivity
import com.example.superspan.ui.activity.MainActivity

class ProductFragment : Fragment() {

    var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product, container, false)



        val btnPlus = view.findViewById<ImageView>(R.id.btn_plus)
        val btnMinus = view.findViewById<ImageView>(R.id.btn_minus)
        val btnBack = view.findViewById<LinearLayout>(R.id.btn_back)
        val numProd = view.findViewById<TextView>(R.id.productCount)

        btnPlus.setOnClickListener {
            addOne(numProd)
        }
        btnMinus.setOnClickListener {
            minusOne(numProd)
        }

        btnBack.setOnClickListener {
            home()
        }

        return view
    }

    private fun addOne(numProdotti: TextView){
        score++
        numProdotti.text = "$score"
    }
    private fun minusOne(numProdotti: TextView){
        if(score != 0) score--
        numProdotti.text = "$score"
    }

    private fun home() {
        // Creiamo un'istanza del fragment
        val fragment = HomeFragment()

        // Lo inseriamo nel contenitore dell'Activity
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment) // 'fragment_container' è l'ID nel tuo XML
            addToBackStack(null) // Opzionale: permette di tornare indietro con il tasto back
            commit()
        }


    }
}