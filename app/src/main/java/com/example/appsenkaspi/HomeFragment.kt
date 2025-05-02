package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdicionarPilar: CardView
    private lateinit var adapter: PilarAdapter

    private val pilarViewModel: PilarViewModel by activityViewModels()

    private var funcionarioLogadoId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.graybar)
        activity?.window?.let { window ->
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        }

        recyclerView = view.findViewById(R.id.recyclerViewPilares)
        cardAdicionarPilar = view.findViewById(R.id.cardAdicionarPilar)

        adapter = PilarAdapter { pilar ->
            abrirTelaPilar(pilar)
        }



        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista) }

        funcionarioLogadoId = arguments?.getInt("funcionarioId") ?: -1



        cardAdicionarPilar.setOnClickListener {
            val fragment = CriarPilarFragment().apply {
                arguments = Bundle().apply {
                    putInt("funcionarioId", funcionarioLogadoId)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun abrirTelaPilar(pilar: PilarEntity) {
        val fragment = TelaPilarFragment().apply {
            arguments = Bundle().apply {
                putInt("pilarId", pilar.id)
                putInt("funcionarioId", funcionarioLogadoId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
