package com.example.appsenkaspi

import RequisicaoAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificacaoFragment : Fragment() {

    private val viewModel: NotificacaoViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private lateinit var adapter: RequisicaoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_notificacoes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotificacoes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            val modoCoordenador = funcionario?.cargo == Cargo.COORDENADOR

            adapter = RequisicaoAdapter(modoCoordenador) { requisicao ->
                if (modoCoordenador) {
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

            if (modoCoordenador) {
                viewModel.getRequisicoesPendentes().observe(viewLifecycleOwner) { lista ->
                    adapter.submitList(lista)
                }
            } else {
                val funcionarioId = funcionario?.id ?: -1
                viewModel.getNotificacoesDoApoio(funcionarioId).observe(viewLifecycleOwner) { lista ->
                    adapter.submitList(lista)
                }
            }
        }
    }
}
