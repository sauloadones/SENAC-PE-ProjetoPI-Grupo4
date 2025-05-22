package com.example.appsenkaspi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.databinding.FragmentHistoricoBinding
import kotlinx.coroutines.launch

class HistoricoFragment : Fragment(R.layout.fragment_historico) {

  private var _binding: FragmentHistoricoBinding? = null
  private val binding get() = _binding!!
  private var statusFiltro: StatusPilar? = null

  private val pilarViewModel: PilarViewModel by activityViewModels()
  private lateinit var adapter: HistoricoAdapter

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    _binding = FragmentHistoricoBinding.bind(view)

    configurarRecyclerView()
    configurarBotoes()
    carregarPilares()
  }

  private fun configurarRecyclerView() {
    adapter = HistoricoAdapter { pilar ->
      abrirTelaDetalhes(pilar)
    }
    binding.recyclerHistoricoPilares.layoutManager = LinearLayoutManager(requireContext())
    binding.recyclerHistoricoPilares.adapter = adapter
  }

  private fun configurarBotoes() {
    // Botão "Todos" - Reseta o filtro
    binding.botaoTodos.setOnClickListener {
      statusFiltro = null
      atualizarTextoDoFiltro(null)
      carregarPilares()
    }

    // Botão do ícone de filtro - abre o BottomSheet
    binding.botaoFiltro.setOnClickListener {
      BottomSheetFiltroStatusFragment(statusFiltro) { novoStatus ->
        statusFiltro = novoStatus
        atualizarTextoDoFiltro(novoStatus)
        aplicarFiltro()
      }.show(parentFragmentManager, "FiltroStatus")
    }
  }

  private fun carregarPilares() {
    // Atualiza status antes de listar
    lifecycleScope.launch {
      pilarViewModel.atualizarStatusDeTodosOsPilares()
    }

    pilarViewModel.listarPilaresHistorico().observe(viewLifecycleOwner) { pilares ->
      adapter.submitList(pilares)
      binding.emptyStateView.visibility = if (pilares.isEmpty()) View.VISIBLE else View.GONE
    }
  }

  private fun aplicarFiltro() {
    pilarViewModel.listarPilaresHistorico().observe(viewLifecycleOwner) { lista ->
      val filtrados = statusFiltro?.let { status ->
        lista.filter { it.status == status }
      } ?: lista

      adapter.submitList(filtrados)
      binding.emptyStateView.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
    }
  }

  private fun atualizarTextoDoFiltro(status: StatusPilar?) {
    val texto = when (status) {
      StatusPilar.CONCLUIDO -> "Concluídos"
      StatusPilar.VENCIDO -> "Vencidos"
      StatusPilar.EXCLUIDO -> "Deletados"
      else -> "Todos"
    }
    binding.botaoTodos.text = texto
  }

  private fun abrirTelaDetalhes(pilar: PilarEntity) {
    val fragment = TelaPilarFragment().apply {
      arguments = Bundle().apply {
        putInt("pilarId", pilar.id)
        putBoolean("modoHistorico", true)
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
