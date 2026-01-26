package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.AddressAdapter
import com.example.superspan.viewmodel.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.ViewModelProvider

class AddressListFragment : Fragment() {
    private lateinit var vm: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_address_list, container, false)

        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAllAddresses)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAddAddress)

        vm.addresses.observe(viewLifecycleOwner) { list ->
            recyclerView.adapter = AddressAdapter(list) { selected ->
                vm.selectAddress(selected) // Logic to set the new default
                parentFragmentManager.popBackStack() // Go back to Order screen
            }
        }

        fabAdd.setOnClickListener {
            // Navigate to "Add New Address" Fragment
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