package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificacaoFragment : Fragment() {

  private val viewModel: NotificacaoViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private lateinit var adapter: RequisicaoAdapter

  // ✅ Guarda o ID do funcionário logado para uso no onStop
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
      funcionarioIdAtual = funcionarioId  // ✅ Armazena para uso posterior
      val modoCoordenador = funcionario.cargo == Cargo.COORDENADOR

      adapter = RequisicaoAdapter(
        funcionarioIdLogado = funcionarioId,
        modoCoordenador = modoCoordenador
      ) { requisicao ->
        if (modoCoordenador && requisicao.tipo != TipoRequisicao.ATIVIDADE_PARA_VENCER) {
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

      // ✅ Atualiza visibilidade da mensagem de "vazio"
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
            val notificacoesDePrazo = pessoais.filter {
              it.tipo == TipoRequisicao.ATIVIDADE_PARA_VENCER
            }
            val listaFinal = pendentes + notificacoesDePrazo
            adapter.submitList(listaFinal)
          }
        }
      } else {
        viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
          adapter.submitList(lista)
          if (lista.isNotEmpty()) {
            recyclerView.post {
              viewModel.marcarTodasComoVistas(funcionarioId)
            }
          }
        }
      }
    }
  }

  // ✅ Marca notificações de prazo como vistas apenas ao sair da tela
  override fun onStop() {
    super.onStop()
    funcionarioIdAtual?.let { id ->
      viewModel.marcarNotificacoesDePrazoComoVistas(id)
    }
  }
}
