package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.widget.ImageView
import android.widget.TextView
import com.example.superspan.R
import com.example.superspan.viewmodel.HomeViewModel

class ProductFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_IMAGE_RES = "arg_image_res"
        private const val ARG_INDEX = "arg_index"

        fun newInstance(
            name: String,
            desc: String,
            price: String,
            imageRes: Int,
            index: Int
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

        // Bind testo/immagine conferiti dall'adapter
        v.findViewById<TextView>(R.id.product_name).text = productName
        v.findViewById<TextView>(R.id.product_description).text = productDesc
        v.findViewById<TextView>(R.id.product_price).text = productPrice
        v.findViewById<ImageView>(R.id.imgProduct).apply {
            if (productImageRes != 0) setImageResource(productImageRes)
        }

        // Back
        v.findViewById<AppCompatImageView>(R.id.btnBackTop).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // btnFavTop è placeholder

        // Qty
        val btnPlus = v.findViewById<AppCompatImageView>(R.id.btnPlus)
        val btnMinus = v.findViewById<AppCompatImageView>(R.id.btnMinus)
        val txtCount = v.findViewById<TextView>(R.id.txtCount)

        // Stato iniziale
        val p0 = vm.products.value?.getOrNull(productIndex)
        txtCount.text = (p0?.qty ?: 0).toString()

        btnPlus.setOnClickListener {
            val p = vm.products.value?.getOrNull(productIndex) ?: return@setOnClickListener
            p.qty += 1
            txtCount.text = p.qty.toString()
            vm.updateCartTotal()
            // ri-emetti per aggiornare le liste quando torni indietro
            vm.products.value = vm.products.value
        }

        btnMinus.setOnClickListener {
            val p = vm.products.value?.getOrNull(productIndex) ?: return@setOnClickListener
            if (p.qty > 0) {
                p.qty -= 1
                txtCount.text = p.qty.toString()
                vm.updateCartTotal()
                vm.products.value = vm.products.value
            }
        }

        return v
    }
}

