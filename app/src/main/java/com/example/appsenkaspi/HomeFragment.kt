package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.example.appsenkaspi.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null
  private val binding get() = _binding ?: throw IllegalStateException("Binding is null")

  private lateinit var recyclerView: RecyclerView
  private lateinit var cardAdicionarPilar: CardView
  private lateinit var adapter: PilarAdapter

  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val pilarViewModel: PilarViewModel by activityViewModels()
  private val atividadeViewModel: AtividadeViewModel by activityViewModels()
  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  private var funcionarioLogadoId: Int = -1

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val iconHistorico = binding.containerHistorico.findViewById<ImageView>(R.id.icon_historico)
    iconHistorico.setOnClickListener {


      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, HistoricoFragment())
        .addToBackStack(null)
        .commit()
    }



    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      funcionario?.let {
        funcionarioLogadoId = it.id

        configurarNotificacaoBadge(
          rootView = view,
          lifecycleOwner = viewLifecycleOwner,
          fragmentManager = parentFragmentManager,
          funcionarioId = it.id,
          cargo = it.cargo,
          viewModel = notificacaoViewModel
        )

        binding.cardAdicionarPilar.visibility =
          if (it.cargo == Cargo.COORDENADOR) View.VISIBLE else View.GONE

        recyclerView = binding.recyclerViewPilares
        cardAdicionarPilar = binding.cardAdicionarPilar

        adapter = PilarAdapter(
          onClickPilar = { pilar -> abrirTelaPilar(pilar) },
          verificarSubpilares = { pilarId -> pilarViewModel.temSubpilaresDireto(pilarId) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Atualiza os status antes de observar os pilares
        lifecycleScope.launch {
          pilarViewModel.atualizarStatusDeTodosOsPilares()
        }

        // Observa apenas pilares ativos
        pilarViewModel.listarPilaresAtivos().observe(viewLifecycleOwner) { lista ->
          adapter.submitList(lista)
        }

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

        requireActivity().window.statusBarColor =
          ContextCompat.getColor(requireContext(), R.color.graybar)
      }
    }
  }

  private fun abrirTelaPilar(pilar: PilarEntity) {
    viewLifecycleOwner.lifecycleScope.launch {
      val temSubpilares = pilarViewModel.temSubpilares(pilar.id)

      val fragment = if (temSubpilares) {
        TelaPilarComSubpilaresFragment().apply {
          arguments = Bundle().apply { putInt("pilarId", pilar.id) }
        }
      } else {
        TelaPilarFragment().apply {
          arguments = Bundle().apply {
            putInt("pilarId", pilar.id)
            putInt("funcionarioId", funcionarioLogadoId)
          }
        }
      }

      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, fragment)
        .addToBackStack(null)
        .commit()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
