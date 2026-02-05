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

/**
 * Fragment che gestisce la sezione dei Preferiti.
 * Osserva i cambiamenti nel ViewModel per aggiornare la lista di Michele in tempo reale.
 */
class FavouriteSectionFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Utilizziamo requireActivity() per condividere lo stesso ViewModel tra Fragment diversi
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
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

        // Inizializzazione delle View
        recyclerView = view.findViewById(R.id.rvFavorites)
        emptyStateText = view.findViewById(R.id.tvEmpty)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Inizializziamo l'adapter con una lista vuota e definiamo le azioni (lambda)
        favouriteAdapter = FavouriteAdapter(
            items = emptyList(),
            onRemoveFavorite = { product -> viewModel.toggleFavoriteByRef(product) },
            onOpenDetail = { product ->
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favouriteAdapter
    }

    private fun observeViewModel() {
        // Osserva la lista dei preferiti nel ViewModel
        viewModel.favorites.observe(viewLifecycleOwner) { favoritesList ->
            // Aggiorna i dati nell'adapter (Usa updateItems come definito nel nuovo Adapter)
            favouriteAdapter.updateItems(favoritesList)

            // Gestione del feedback visivo: se la lista è vuota, mostra il messaggio di aiuto
            emptyStateText.visibility = if (favoritesList.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }
}