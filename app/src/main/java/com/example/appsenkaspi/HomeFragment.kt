package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
<<<<<<< Updated upstream

=======
import com.example.appsenkaspi.databinding.FragmentHomeBinding

@Suppress("DEPRECATION")
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======

        // Define a cor da status bar
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.graybar)

        configurarBotaoSino(view, parentFragmentManager)

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {
                Cargo.APOIO -> {
                    binding.cardAdicionarPilar.visibility = View.GONE
                }
                Cargo.COORDENADOR -> {
                    binding.cardAdicionarPilar.visibility = View.VISIBLE
                }
                Cargo.GESTOR -> {
                    binding.cardAdicionarPilar.visibility = View.GONE
                }
                else -> {
                    binding.cardAdicionarPilar.visibility = View.GONE
                }
            }
        }
>>>>>>> Stashed changes

        recyclerView = view.findViewById(R.id.recyclerViewPilares)
        cardAdicionarPilar = view.findViewById(R.id.cardAdicionarPilar)

        adapter = PilarAdapter { pilar ->
            abrirTelaPilar(pilar)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

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
<<<<<<< Updated upstream
=======

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
>>>>>>> Stashed changes
}
