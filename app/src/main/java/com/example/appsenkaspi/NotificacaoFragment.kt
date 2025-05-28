package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificacaoFragment : Fragment() {

  private val viewModel: NotificacaoViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private lateinit var adapter: RequisicaoAdapter
  private var funcionarioIdAtual: Int? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_notificacoes, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotificacoes)
    val vazio = view.findViewById<TextView>(R.id.textVazioNotificacoes)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
      val funcionarioId = funcionario?.id ?: return@observe
      funcionarioIdAtual = funcionarioId
      val modoCoordenador = funcionario.cargo == Cargo.COORDENADOR

      adapter = RequisicaoAdapter(
        funcionarioIdLogado = funcionarioId,
        modoCoordenador = modoCoordenador
      ) { requisicao ->
        if (modoCoordenador && requisicao.tipo != TipoRequisicao.ATIVIDADE_PARA_VENCER && requisicao.tipo != TipoRequisicao.ATIVIDADE_VENCIDA) {
          val fragment = DetalheNotificacaoFragment().apply {
            arguments = Bundle().apply {
              putInt("requisicaoId", requisicao.id)
            }
          }
          requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
        }
      }

      recyclerView.adapter = adapter

      adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
          vazio.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        }
      })

      if (modoCoordenador) {
        val pendentesLiveData = viewModel.getRequisicoesPendentes()
        val pessoaisLiveData = viewModel.getNotificacoesDoApoio(funcionarioId)

        pendentesLiveData.observe(viewLifecycleOwner) { pendentes ->
          pessoaisLiveData.observe(viewLifecycleOwner) { pessoais ->
            val notificacoesAuto = pessoais.filter {
              it.tipo in listOf(
                TipoRequisicao.ATIVIDADE_PARA_VENCER,
                TipoRequisicao.ATIVIDADE_VENCIDA,
                TipoRequisicao.PRAZO_ALTERADO,
                TipoRequisicao.ATIVIDADE_CONCLUIDA,
                TipoRequisicao.RESPONSAVEL_REMOVIDO,
                TipoRequisicao.RESPONSAVEL_ADICIONADO
              ) && it.solicitanteId == funcionarioId
            }
            val listaFinal = (pendentes + notificacoesAuto).sortedBy { it.resolvida }
              .sortedBy { it.resolvida } // opcional: coloca resolvidas por último
            if (adapter.currentList != listaFinal) {
              adapter.submitList(listaFinal)
            }
          }
        }
      } else {
        viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
          adapter.submitList(lista)
          if (lista.isNotEmpty()) {
            recyclerView.post {
              val notificationManager = NotificationManagerCompat.from(requireContext())

              lista.filter {
                it.tipo == TipoRequisicao.ATIVIDADE_VENCIDA && !it.foiVista && it.solicitanteId == funcionarioId
              }.forEach { requisicao ->
                notificationManager.cancel(requisicao.id)
                viewModel.marcarTodasComoVistas(requisicao.id)
              }

              viewModel.marcarTodasComoVistas(funcionarioId)
            }
          }
        }
      }
    }
  }

  private fun atualizarListaNotificacoes() {
    val funcionarioId = funcionarioIdAtual ?: return
    val modoCoordenador = funcionarioViewModel.funcionarioLogado.value?.cargo == Cargo.COORDENADOR

    if (modoCoordenador) {
      val pendentesLiveData = viewModel.getRequisicoesPendentes()
      val pessoaisLiveData = viewModel.getNotificacoesDoApoio(funcionarioId)

      pendentesLiveData.observe(viewLifecycleOwner) { pendentes ->
        pessoaisLiveData.observe(viewLifecycleOwner) { pessoais ->
          val notificacoesDePrazoOuVencida = pessoais.filter {
            it.tipo in listOf(
              TipoRequisicao.ATIVIDADE_PARA_VENCER,
              TipoRequisicao.ATIVIDADE_VENCIDA,
              TipoRequisicao.PRAZO_ALTERADO,
              TipoRequisicao.ATIVIDADE_CONCLUIDA,
              TipoRequisicao.RESPONSAVEL_REMOVIDO,
              TipoRequisicao.RESPONSAVEL_ADICIONADO,

            ) && it.solicitanteId == funcionarioId
          }
          val listaFinal = (pendentes + notificacoesDePrazoOuVencida)
            .sortedBy { it.resolvida }
          adapter.submitList(listaFinal)
        }
      }
    } else {
      viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
        adapter.submitList(lista)
      }
    }
  }

  override fun onStop() {
    super.onStop()
    funcionarioIdAtual?.let { id ->
      viewModel.marcarNotificacoesDePrazoComoVistas(id)
    }
  }

  override fun onResume() {
    super.onResume()
    atualizarListaNotificacoes()
  }
}
