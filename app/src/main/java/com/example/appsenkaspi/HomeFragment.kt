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

        // Notificação e badge (se tiver)

        // Configura badge de notificação
        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { f ->
          f?.let { func ->
            configurarNotificacaoBadge(
              rootView = view,
              lifecycleOwner = viewLifecycleOwner,
              fragmentManager = parentFragmentManager,
              funcionarioId = func.id,
              cargo = func.cargo,
              viewModel = notificacaoViewModel
            )
          }
        }

        // Controle de visibilidade do botão "Adicionar Pilar"
        binding.cardAdicionarPilar.visibility = when (it.cargo) {
          Cargo.COORDENADOR -> View.VISIBLE
          else -> View.GONE
        }

        // RecyclerView de pilares
        recyclerView = view.findViewById(R.id.recyclerViewPilares)
        cardAdicionarPilar = view.findViewById(R.id.cardAdicionarPilar)


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


        // Filtrar só planejados e em andamento
        pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
          val listaFiltrada = lista.filter { pilar ->
            pilar.status == StatusPilar.PLANEJADO || pilar.status == StatusPilar.EM_ANDAMENTO
          }
          adapter.submitList(listaFiltrada)
        }

        // Botão para criar novo pilar

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


        // Botão histórico (box_historico)
        val boxHistorico = view.findViewById<View>(R.id.box_historico)
        boxHistorico.setOnClickListener {
          val historicoFragment = HistoricoFragment()
          parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, historicoFragment)
            .addToBackStack(null)
            .commit()
        }

        // Cor da barra de status do Android

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
