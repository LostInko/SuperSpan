package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.AddressAdapter
import com.example.superspan.viewmodel.HomeViewModel

class FragmentOrder : Fragment() {

    private lateinit var vm : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order, container, false)

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


    }

}