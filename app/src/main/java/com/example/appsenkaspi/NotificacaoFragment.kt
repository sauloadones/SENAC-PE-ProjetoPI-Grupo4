package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class NotificacaoFragment : Fragment() {

  private val viewModel: NotificacaoViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private lateinit var adapter: RequisicaoAdapter
  private var funcionarioIdAtual: Int? = null
  private var modoSelecaoAtivo = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.fragment_notificacoes, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotificacoes)
    val vazio = view.findViewById<TextView>(R.id.textVazioNotificacoes)
    val trashIcon = view.findViewById<ImageView>(R.id.trashIcon)

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

      // Observação das notificações:
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
            val listaFinal = (pendentes + notificacoesAuto)
              .filter { !it.excluida }
              .sortedBy { it.resolvida }

            if (adapter.currentList != listaFinal) {
              adapter.submitList(listaFinal)
            }
          }
        }
      } else {
        viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
          val listaFiltrada = lista.filter { !it.excluida }
          adapter.submitList(listaFiltrada)

          if (listaFiltrada.isNotEmpty()) {
            recyclerView.post {
              val notificationManager = NotificationManagerCompat.from(requireContext())

              listaFiltrada.filter {
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

      // 🔴 Clique no botão da lixeira
      trashIcon.setOnClickListener {
        if (!modoSelecaoAtivo) {
          modoSelecaoAtivo = true
          adapter.modoSelecao = true
          adapter.selecionadas.clear()
          adapter.notifyDataSetChanged()
          trashIcon.setImageResource(R.drawable.ic_delete)
        } else {
          if (adapter.selecionadas.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
              .setTitle("Excluir notificações")
              .setMessage("Tem certeza que deseja excluir ${adapter.selecionadas.size} notificações selecionadas?")
              .setPositiveButton("Excluir") { dialog, _ ->
                viewModel.excluirRequisicoes(adapter.selecionadas.toList())
                Snackbar.make(view, "Notificações excluídas", Snackbar.LENGTH_SHORT).show()

                modoSelecaoAtivo = false
                adapter.modoSelecao = false
                adapter.selecionadas.clear()
                adapter.notifyDataSetChanged()
                trashIcon.setImageResource(R.drawable.ic_delete)
                dialog.dismiss()
              }
              .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
              }
              .show()
          } else {
            modoSelecaoAtivo = false
            adapter.modoSelecao = false
            adapter.notifyDataSetChanged()
            trashIcon.setImageResource(R.drawable.ic_delete)
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
            .filter { !it.excluida }
            .sortedBy { it.resolvida }

          adapter.submitList(listaFinal)
        }
      }
    } else {
      viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
        adapter.submitList(lista.filter { !it.excluida })
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
