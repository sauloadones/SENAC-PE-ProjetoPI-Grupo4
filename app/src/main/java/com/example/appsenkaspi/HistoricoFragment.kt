package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels

class HistoricoFragment : Fragment() {

    private lateinit var adapter: TelaHistoricoAdapter
    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private var funcionarioLogadoId: Int = -1
    private var listaOriginal: List<PilarEntity> = emptyList()


    private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

    // Filtro pelo status selecionado (null = todos os 3 do histórico)
    private var filtroStatus: StatusPilar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)


        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            funcionario?.let {
                configurarNotificacaoBadge(
                    rootView = view,
                    lifecycleOwner = viewLifecycleOwner,
                    fragmentManager = parentFragmentManager,
                    funcionarioId = it.id,
                    cargo = it.cargo,
                    viewModel = notificacaoViewModel
                )
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFiltroExclusao)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TelaHistoricoAdapter(emptyList()) { pilar ->
            abrirTelaPilar(pilar)
        }
        recyclerView.adapter = adapter

        val spinnerStatus = view.findViewById<Spinner>(R.id.spinnerStatusFiltro)
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                filtroStatus = when (position) {
                    1 -> StatusPilar.CONCLUIDO
                    2 -> StatusPilar.EXCLUIDO
                    3 -> StatusPilar.VENCIDO
                    else -> null // "Todos"
                }
                aplicarFiltroStatus()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                filtroStatus = null
                aplicarFiltroStatus()
            }
        }

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            funcionario?.let {
                funcionarioLogadoId = it.id
            }
        }

        // Aqui aplicamos o filtro de histórico diretamente
        pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
            listaOriginal = lista.filter { pilar ->
                pilar.status == StatusPilar.CONCLUIDO ||
                        pilar.status == StatusPilar.EXCLUIDO ||
                        pilar.status == StatusPilar.VENCIDO
            }
            aplicarFiltroStatus()
        }
    }

    private fun aplicarFiltroStatus() {
        val filtrados = if (filtroStatus == null) {
            listaOriginal
        } else {
            listaOriginal.filter { it.status == filtroStatus }
        }
        adapter.atualizarLista(filtrados)
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
}