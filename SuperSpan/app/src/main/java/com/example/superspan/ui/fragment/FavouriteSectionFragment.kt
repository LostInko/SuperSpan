
package com.example.superspan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superspan.R
import com.example.superspan.adapter.FavouriteAdapter
import com.example.superspan.viewmodel.HomeViewModel

class FavouriteSectionFragment : Fragment() {

    private lateinit var vm: HomeViewModel
    private lateinit var rv: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvFavorites)
        emptyView = view.findViewById(R.id.tvEmpty)

        adapter = FavouriteAdapter(
            items = emptyList(),
            onRemoveFavorite = { p ->
                vm.toggleFavoriteByRef(p) // toglie dai preferiti
            },
            onOpenDetail = { p ->
                // Apri il ProductFragment coerente con il tuo costruttore
                val index = vm.products.value?.indexOfFirst { it.name == p.name } ?: -1
                val frag = com.example.superspan.ui.fragment.ProductFragment.newInstance(
                    name = p.name,
                    desc = p.description,
                    price = p.price,
                    imageRes = p.imageRes,
                    index = index
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit()
            }
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        vm.favorites.observe(viewLifecycleOwner) { favs ->
            adapter.submit(favs)
            emptyView.visibility = if (favs.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }
}

