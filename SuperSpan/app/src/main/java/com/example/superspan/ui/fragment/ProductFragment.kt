package com.example.superspan.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.model.Product
import com.example.superspan.viewmodel.HomeViewModel

/**
 * Fragment di dettaglio. Qui Michele verifica la convenienza reale di un prodotto
 * prima di decidere se inserirlo nella lista dei preferiti o nel carrello.
 */
class ProductFragment : Fragment() {

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_IMAGE_RES = "arg_image_res"
        private const val ARG_INDEX = "arg_index"

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

    private lateinit var viewModel: HomeViewModel

    // Estrazione dei dati dagli argomenti. "lazy" significa che vengono calcolati solo quando servono.
    private val productIndex: Int by lazy { arguments?.getInt(ARG_INDEX, -1) ?: -1 }
    private val productName: String by lazy { arguments?.getString(ARG_NAME).orEmpty() }
    private val productDesc: String by lazy { arguments?.getString(ARG_DESC).orEmpty() }
    private val productImageRes: Int by lazy { arguments?.getInt(ARG_IMAGE_RES) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aggancio al ViewModel dell'Activity per condividere i dati tra i vari fragment (Home, Preferiti, Carrello)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        // Risolviamo il riferimento al prodotto corrente per gestire stati come 'isFavorite' o 'qty'
        val currentProduct = resolveCurrentProduct()

        // Inizializzazione modulare delle varie parti della schermata
        setupToolbar(view, currentProduct)
        setupProductDetails(view, currentProduct)
        setupQuantityControls(view, currentProduct)

        return view
    }

    /**
     * Configura la barra superiore: tasto back e pulsante preferiti.
     */
    private fun setupToolbar(view: View, product: Product?) {
        // Gestione chiusura fragment tramite il back stack di Android
        view.findViewById<AppCompatImageView>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val btnFavTop = view.findViewById<AppCompatImageView>(R.id.btnFavTop)

        product?.let { p ->
            // Imposta l'icona del cuore (pieno o vuoto) in base allo stato attuale del prodotto
            updateFavIcon(btnFavTop, p.isFavorite)

            btnFavTop?.setOnClickListener {
                // Inverte lo stato del preferito nel ViewModel
                viewModel.toggleFavoriteByRef(p)
                // Aggiorna immediatamente l'icona per dare feedback a Michele
                updateFavIcon(btnFavTop, p.isFavorite)
            }
        }
    }

    /**
     * Riempie i campi di testo e gestisce la logica visiva dello sconto.
     */
    private fun setupProductDetails(view: View, product: Product?) {
        val txtName = view.findViewById<TextView>(R.id.product_name)
        val txtDesc = view.findViewById<TextView>(R.id.product_description)
        val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)
        val txtPrice = view.findViewById<TextView>(R.id.product_price)
        val txtOldPrice = view.findViewById<TextView>(R.id.product_old_price)

        // Assegnazione dati base (nome, descrizione, immagine)
        txtName?.text = product?.name ?: productName
        txtDesc?.text = product?.description ?: productDesc
        if (productImageRes != 0) imgProduct?.setImageResource(productImageRes)

        // LOGICA SCONTI: Michele vuole vedere chiaramente quanto risparmia
        if (product?.discountPrice != null) {
            // Se in offerta: prezzo attuale in rosso
            txtPrice?.text = product.discountPrice
            txtPrice?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

            // Mostra il prezzo originale sbarrato
            txtOldPrice?.visibility = View.VISIBLE
            txtOldPrice?.text = product.price
            // STRIKE_THRU_TEXT_FLAG aggiunge graficamente la linea sopra il testo
            txtOldPrice?.paintFlags = txtOldPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // Se a prezzo pieno: colore nero standard e nasconde il campo "prezzo vecchio"
            txtPrice?.text = product?.price
            txtPrice?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            txtOldPrice?.visibility = View.GONE
        }
    }

    /**
     * Gestisce i pulsanti + e - per la quantità nel carrello.
     */
    private fun setupQuantityControls(view: View, product: Product?) {
        val btnPlus = view.findViewById<AppCompatImageView>(R.id.btnPlus)
        val btnMinus = view.findViewById<AppCompatImageView>(R.id.btnMinus)
        val txtCount = view.findViewById<TextView>(R.id.txtCount)

        // Carica la quantità attuale (se già presente nel carrello)
        txtCount?.text = (product?.qty ?: 0).toString()

        btnPlus?.setOnClickListener {
            product?.let {
                it.qty += 1
                txtCount?.text = it.qty.toString()
                notifyChanges() // Aggiorna i totali dell'app
            }
        }

        btnMinus?.setOnClickListener {
            product?.let {
                if (it.qty > 0) {
                    it.qty -= 1
                    txtCount?.text = it.qty.toString()
                    notifyChanges() // Aggiorna i totali dell'app
                }
            }
        }
    }

    /**
     * Cambia la risorsa immagine del pulsante preferiti.
     */
    private fun updateFavIcon(imageView: AppCompatImageView?, isFav: Boolean) {
        imageView?.setImageResource(
            if (isFav) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    /**
     * Cerca il prodotto corretto all'interno della lista globale del ViewModel
     * per assicurarsi di modificare l'oggetto reale e non una copia.
     */
    private fun resolveCurrentProduct(): Product? {
        val list = viewModel.products.value ?: return null
        // Cerca per indice se valido, altrimenti per nome
        return if (productIndex in list.indices) {
            list[productIndex]
        } else {
            list.find { it.name == productName }
        }
    }

    /**
     * Forza il ViewModel ad aggiornare il totale del carrello e notificare
     * tutti gli altri Fragment (Home, Carrello) del cambiamento.
     */
    private fun notifyChanges() {
        viewModel.updateCartTotal()
        // Re-impostando il valore della lista, si triggerano gli 'observe' attivi nell'app
        viewModel.products.value = viewModel.products.value
    }
}