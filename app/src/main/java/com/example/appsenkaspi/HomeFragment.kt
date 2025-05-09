package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import com.example.appsenkaspi.databinding.FragmentHomeBinding

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

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      funcionario?.let {
        val funcionarioId = it.id
        funcionarioLogadoId = funcionarioId

        // ✅ Verifica e gera requisições de conclusão automática


        // ✅ Configura o contador de notificações não vistas no badge
        inicializarNotificacaoBadge(
          rootView = view,
          lifecycleOwner = viewLifecycleOwner,
          fragmentManager = parentFragmentManager,
          funcionarioId = funcionarioLogadoId,
          viewModel = notificacaoViewModel
        )

        // ✅ Configura botão do sino
        configurarBotaoSino(view, parentFragmentManager, funcionarioId, notificacaoViewModel)

        // ✅ Visibilidade do botão "Adicionar Pilar" conforme o cargo
        binding.cardAdicionarPilar.visibility = when (it.cargo) {
          Cargo.COORDENADOR -> View.VISIBLE
          else -> View.GONE
        }
      }
    }

    // ✅ RecyclerView de pilares
    recyclerView = view.findViewById(R.id.recyclerViewPilares)
    cardAdicionarPilar = view.findViewById(R.id.cardAdicionarPilar)

    adapter = PilarAdapter { pilar -> abrirTelaPilar(pilar) }
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter

    pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
      adapter.submitList(lista)
    }

    // ✅ Ação do botão para adicionar pilar
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

    // ✅ Cor da status bar
    requireActivity().window.statusBarColor =
      ContextCompat.getColor(requireContext(), R.color.graybar)
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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
