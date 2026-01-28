package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.superspan.R
import com.example.superspan.ui.activity.GlobalData
import com.example.superspan.viewmodel.HomeViewModel
import androidx.core.graphics.toColorInt
import com.example.superspan.model.Order
import com.google.android.material.card.MaterialCardView

class OrderConfirmationFragment : Fragment() {

    private lateinit var vm : HomeViewModel

    private var tvCartAmountInActivity: TextView? = null
    private var tvTotalPrice: TextView? = null
    private var paymentMethod: String = "CREDIT_CARD"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_confirmation, container, false)

        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        val tvSelectedShop = view.findViewById<TextView>(R.id.tvSelectedShop)
        tvSelectedShop.text = GlobalData.selectedShop

        val tvSelectedAddressName = view.findViewById<TextView>(R.id.tvSelectedAddressName)
        val tvSelectedAddress = view.findViewById<TextView>(R.id.tvSelectedAddress)
        vm.addresses.observe(viewLifecycleOwner) { allAddresses ->
            val defaultAddress = allAddresses.find { it.isSelected }

            if (defaultAddress != null) {
                tvSelectedAddressName.text = defaultAddress.Name
                tvSelectedAddress.text = "${defaultAddress.Address}, ${defaultAddress.City}"
            }
        }

        val cardCreditCard = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardCreditCard)
        val cardCash = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardCash)

        // Listener sui click
        cardCreditCard.setOnClickListener { selectPayment("CREDIT_CARD", view) }
        cardCash.setOnClickListener { selectPayment("CASH", view) }

        tvTotalPrice = view.findViewById<TextView>(R.id.tv_total_price)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCartAmountInActivity = requireActivity().findViewById(R.id.tv_cart_amount)

        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        vm.cartTotal.observe(viewLifecycleOwner) { total ->
            val formatted = String.format("%.2f €", total)
            tvCartAmountInActivity?.text = formatted
            tvTotalPrice?.text = formatted
        }

        val btnPay = view.findViewById<android.widget.Button>(R.id.btnPay)
        btnPay.setOnClickListener {
            val cartProducts = vm.products.value?.filter { it.qty > 0 }?.map { it.copy() } ?: emptyList()

            val selectedAddress = vm.getSelectedAddress()
            val selectedShop = GlobalData.selectedShop ?: "SuperSpan Store"

            if (cartProducts.isNotEmpty() && selectedAddress != null) {

                // Crea l'ordine con i prodotti CLONATI
                val newOrder = Order(
                    products = cartProducts,
                    address = selectedAddress,
                    shop = selectedShop
                )
                vm.addOrder(newOrder)

                vm.clearCart()

                Toast.makeText(requireContext(), "Ordine completato!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeSectionFragment())
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Errore: Carrello vuoto o indirizzo mancante", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun selectPayment(method: String, view: View) {
        paymentMethod = method

        val cardCreditCard = view.findViewById<MaterialCardView>(R.id.cardCreditCard)
        val cardCash = view.findViewById<MaterialCardView>(R.id.cardCash)
        val tvCreditCard = view.findViewById<TextView>(R.id.tvCreditCard)
        val tvCash = view.findViewById<TextView>(R.id.tvCash)

        val colorGreenText = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.greenText)
        val colorGreenIcon = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.greenIcon)
        val colorWhite = androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.white)
        val colorInactiveText = "#A0A0A0".toColorInt()

        if (method == "CREDIT_CARD") {
            // --- CARTA DI CREDITO SELEZIONATA (Attiva) ---
            cardCreditCard.strokeWidth = dpToPx(2) // 2dp bordo
            cardCreditCard.setStrokeColor(android.content.res.ColorStateList.valueOf(colorGreenIcon))
            tvCreditCard.setTextColor(colorGreenText)
            tvCreditCard.setTypeface(null, android.graphics.Typeface.BOLD)

            // --- CONTANTI (Disattivo) ---
            cardCash.strokeWidth = 0 // 0dp bordo
            cardCash.setStrokeColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT))
            cardCash.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(colorWhite))
            tvCash.setTextColor(colorInactiveText)
            tvCash.setTypeface(null, android.graphics.Typeface.NORMAL)

        } else {
            // --- CONTANTI SELEZIONATO (Attiva) ---
            cardCash.strokeWidth = dpToPx(2)
            cardCash.setStrokeColor(android.content.res.ColorStateList.valueOf(colorGreenIcon))
            tvCash.setTextColor(colorGreenText)
            tvCash.setTypeface(null, android.graphics.Typeface.BOLD)

            // --- CARTA DI CREDITO (Disattivo) ---
            cardCreditCard.strokeWidth = 0
            cardCreditCard.setStrokeColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT))
            cardCreditCard.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(colorWhite))
            tvCreditCard.setTextColor(colorInactiveText)
            tvCreditCard.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    // Funzione per convertire DP in Pixel per il bordo
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}