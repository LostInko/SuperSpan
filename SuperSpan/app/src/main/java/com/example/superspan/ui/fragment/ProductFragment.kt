package com.example.superspan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.superspan.R

class ProductFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_IMAGE_RES = "arg_image_res"

        // Creo il fragment con i dati del prodotto
        fun newInstance(
            name: String,
            desc: String,
            price: String,
            imageRes: Int
        ): ProductFragment {
            return ProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_DESC, desc)
                    putString(ARG_PRICE, price)
                    putInt(ARG_IMAGE_RES, imageRes)
                }
            }
        }
    }

    private var score = 0

    // Leggo gli argomenti tramite lazy
    private val productName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val productDesc: String by lazy { arguments?.getString(ARG_DESC).orEmpty() }
    private val productPrice: String by lazy { arguments?.getString(ARG_PRICE).orEmpty() }
    private val productImageRes: Int by lazy { arguments?.getInt(ARG_IMAGE_RES) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // niente altro da fare qui
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        val btnPlus = view.findViewById<ImageView>(R.id.btn_plus)
        val btnMinus = view.findViewById<ImageView>(R.id.btn_minus)
        val btnBack = view.findViewById<LinearLayout>(R.id.btn_back)
        val numProd = view.findViewById<TextView>(R.id.productCount)

        // Bind dei dati ricevuti negli argomenti
        view.findViewById<TextView>(R.id.product_name).text = productName
        view.findViewById<TextView>(R.id.product_description).text = productDesc
        view.findViewById<TextView>(R.id.product_price).text = productPrice
        view.findViewById<ImageView>(R.id.imgProduct).apply {
            if (productImageRes != 0) setImageResource(productImageRes)
        }

        btnPlus.setOnClickListener { addOne(numProd) }
        btnMinus.setOnClickListener { minusOne(numProd) }

        // 🔙 Torna indietro alla schermata precedente usando il back stack
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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

    // Rimane disponibile se vuoi navigare "hard" verso la Home
    private fun home() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, HomeSectionFragment())
            addToBackStack(null)
            commit()
        }
    }
}
