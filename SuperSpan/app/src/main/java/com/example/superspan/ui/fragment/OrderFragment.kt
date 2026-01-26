package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.superspan.R
import com.example.superspan.viewmodel.HomeViewModel
import androidx.lifecycle.ViewModelProvider

class OrderFragment : Fragment() {

    private lateinit var vm : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order, container, false)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]


        // Inside onCreateView or onViewCreated
        val tvAddressTitle = view.findViewById<TextView>(R.id.tvAddressTitle)
        val tvAddressDetails = view.findViewById<TextView>(R.id.tvAddressDetails)
        val tvChangeAddress = view.findViewById<TextView>(R.id.tvChangeAddress)


        vm.addresses.observe(viewLifecycleOwner) { allAddresses ->
            // Find the address marked as default
            val defaultAddress = allAddresses.find { it.isSelected }

            if (defaultAddress != null) {
                tvAddressTitle.text = defaultAddress.Name // e.g., "Home"
                tvAddressDetails.text = "${defaultAddress.Address}, ${defaultAddress.City}"
            }
        }

        tvChangeAddress.setOnClickListener {
            // Navigate to the fragment showing all addresses
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddressListFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bottone back (in testa alla pagina)
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            // Oppure, se usi Navigation Component:
            // findNavController().navigateUp()
        }

    }

}