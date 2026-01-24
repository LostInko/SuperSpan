
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
         * Costruttore consigliato: passa anche l'indice se lo conosci.
         * Se non lo hai, usa -1: il fragment farà fallback per nome.
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

        // ---- Bind dati statici (coerenti con l'XML) ----
        v.findViewById<TextView>(R.id.product_name)?.text = productName
        v.findViewById<TextView>(R.id.product_description)?.text = productDesc
        v.findViewById<TextView>(R.id.product_price)?.text = productPrice
        v.findViewById<ImageView>(R.id.imgProduct)?.apply {
            if (productImageRes != 0) setImageResource(productImageRes)
        }

        // ---- Back (ID unico presente: btnBackTop) ----
        v.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // btnFavTop è placeholder

        // ---- Qty (ID presenti nel tuo XML) ----
        val btnPlus = v.findViewById<AppCompatImageView>(R.id.btnPlus)
        val btnMinus = v.findViewById<AppCompatImageView>(R.id.btnMinus)
        val txtCount = v.findViewById<TextView>(R.id.txtCount)

        // Stato iniziale quantità
        val p0 = resolveCurrentProduct()
        txtCount?.text = (p0?.qty ?: 0).toString()

        btnPlus?.setOnClickListener {
            val p = resolveCurrentProduct() ?: return@setOnClickListener
            p.qty += 1
            txtCount?.text = p.qty.toString()
            notifyVmChanged()
        }

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
     * Recupera il prodotto corrente. Preferisce l'indice se valido, altrimenti cerca per nome.
     */
    private fun resolveCurrentProduct(): com.example.superspan.model.Product? {
        val list = vm.products.value ?: return null
        return if (productIndex in 0 until list.size) {
            list[productIndex]
        } else {
            list.find { it.name == productName }
        }
    }

    /**
     * Notifica le variazioni al ViewModel e aggiorna il totale carrello.
     * Mantiene l'approccio della prima versione.
     */
    private fun notifyVmChanged() {
        // Se la tua implementazione del ViewModel non prevede questa funzione,
        // sostituiscila con vm.notifyChange() oppure implementa updateCartTotal() nel VM.
        vm.updateCartTotal()
        vm.products.value = vm.products.value
    }
}
