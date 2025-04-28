package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewPilar : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PilarAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recycler_view_pilar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewPilares)
        adapter = PilarAdapter { pilar ->
            // ação quando clicar no botão do Pilar
            // exemplo: Toast.makeText(requireContext(), pilar.nome, Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        homeViewModel.listaPilaresLiveData.observe(viewLifecycleOwner) { pilares ->
            adapter.submitList(pilares)
        }
    }
}
