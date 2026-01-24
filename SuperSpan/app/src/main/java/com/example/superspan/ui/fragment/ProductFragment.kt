package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.viewmodel.HomeViewModel

class ProductFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_IMAGE_RES = "arg_image_res"
        private const val ARG_INDEX = "arg_index"

        /**
         * Costruttore consigliato: consente di passare anche l'indice
         * (se non lo conosci, passa -1 e useremo il nome come fallback).
         */
        @JvmOverloads
        fun newInstance(
            name: String,
            desc: String,
            price: String,
            imageRes: Int,
            index: Int = -1
        ): ProductFragment {
            return ProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_DESC, desc)
                    putString(ARG_PRICE, price)
                    putInt(ARG_IMAGE_RES, imageRes)
                    putInt(ARG_INDEX, index)
                }
            }
        }
    }

    private lateinit var vm: HomeViewModel

    // Argomenti
    private val productIndex: Int by lazy { arguments?.getInt(ARG_INDEX, -1) ?: -1 }
    private val productName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val productDesc: String by lazy { arguments?.getString(ARG_DESC).orEmpty() }
    private val productPrice: String by lazy { arguments?.getString(ARG_PRICE).orEmpty() }
    private val productImageRes: Int by lazy { arguments?.getInt(ARG_IMAGE_RES) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_product, container, false)

        // ---- Bind dati statici (testi e immagine) ----
        v.findViewById<TextView>(R.id.product_name)?.text = productName
        v.findViewById<TextView>(R.id.product_description)?.text = productDesc
        v.findViewById<TextView>(R.id.product_price)?.text = productPrice
        v.findViewById<ImageView>(R.id.imgProduct)?.apply {
            if (productImageRes != 0) setImageResource(productImageRes)
        }

        // ---- Back: supporta sia btnBackTop (nuovo) sia btn_back (vecchio) ----
        v.findViewById<View?>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        v.findViewById<View?>(R.id.btn_back)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // ---- Qty: supporta entrambi i set di ID ----
        val btnPlus: View? = v.findViewById(R.id.btnPlus) ?: v.findViewById(R.id.btn_plus)
        val btnMinus: View? = v.findViewById(R.id.btnMinus) ?: v.findViewById(R.id.btn_minus)
        val txtCount: TextView? = v.findViewById(R.id.txtCount) ?: v.findViewById(R.id.productCount)

        // Stato iniziale quantità
        val p0 = resolveCurrentProduct()
        txtCount?.text = (p0?.qty ?: 0).toString()

        // +1
        btnPlus?.setOnClickListener {
            val p = resolveCurrentProduct() ?: return@setOnClickListener
            p.qty += 1
            txtCount?.text = p.qty.toString()
            notifyVmChanged()
        }

        // -1
        btnMinus?.setOnClickListener {
            val p = resolveCurrentProduct() ?: return@setOnClickListener
            if (p.qty > 0) {
                p.qty -= 1
                txtCount?.text = p.qty.toString()
                notifyVmChanged()
            }
        }

        return v
    }

    /**
     * Recupera il prodotto corrente. Preferisce l'indice (se valido), altrimenti cerca per nome.
     */
    private fun resolveCurrentProduct(): com.example.superspan.model.Product? {
        val list = vm.products.value
        if (list.isNullOrEmpty()) return null

        return if (productIndex in 0 until list.size) {
            list[productIndex]
        } else {
            list.find { it.name == productName }
        }
    }

    /**
     * Notifica le variazioni al ViewModel e aggiorna il totale carrello.
     * Mantengo la strategia della "prima" versione:
     * - updateCartTotal()
     * - ri-emissione della lista per notificare gli osservatori.
     */
    private fun notifyVmChanged() {
        // Se il tuo ViewModel non ha updateCartTotal(), commenta la riga seguente
        vm.updateCartTotal()

        // Forza la ri-emissione della lista (trigger LiveData)
        vm.products.value = vm.products.value

        // Se preferivi la strategia "seconda versione", puoi aggiungere nel tuo VM:
        // fun notifyChange() { products.value = products.value }
        // e richiamarla qui al posto di quanto sopra.
    }
}
