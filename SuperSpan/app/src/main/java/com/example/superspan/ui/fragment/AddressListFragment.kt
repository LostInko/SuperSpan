package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
                vm.selectAddress(selected)
                parentFragmentManager.popBackStack()
            }
        }

        // Binding dei componenti
        val backButton = view.findViewById<ImageView>(R.id.btnBackTop)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddAddressFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvAllAddresses)
        val tvEmpty = view.findViewById<ConstraintLayout>(R.id.tvEmptyAdd)

        // Bottone back (in testa alla pagina)
        view.findViewById<View>(R.id.btnBackTop)?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        vm.addresses.observe(viewLifecycleOwner) { addresses ->
            if (addresses.isNullOrEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rv.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rv.visibility = View.VISIBLE
            }
        }

    }
}