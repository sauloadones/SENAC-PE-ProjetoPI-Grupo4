package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.util.*

class HistoricoFragment : Fragment() {

    private lateinit var adapter: TelaHistoricoAdapter
    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

    private var funcionarioLogadoId: Int = -1
    private var listaOriginal: List<PilarEntity> = emptyList()

    private var filtroStatusSelecionado: String = "Todos"
    private var filtroAnoSelecionado: String = "Todos"

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

        adapter = TelaHistoricoAdapter(pilarViewModel, emptyList()) { pilar ->
            abrirTelaPilar(pilar)
        }
        recyclerView.adapter = adapter

        val spinnerStatusFilter = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerStatusFilter)
        val spinnerAnoFiltro = view.findViewById<MaterialAutoCompleteTextView>(R.id.spinnerAnoFiltro)

        val itensStatus = resources.getStringArray(R.array.status_pilar_array).toMutableList()
        if (!itensStatus.contains("Todos")) {
            itensStatus.add(0, "Todos")
        }

        val adapterStatus = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item_white,
            itensStatus
        )
        spinnerStatusFilter.setAdapter(adapterStatus)
        spinnerStatusFilter.setText("Todos", false)

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            funcionario?.let {
                funcionarioLogadoId = it.id
            }
        }

        pilarViewModel.listarTodosPilares().observe(viewLifecycleOwner) { lista ->
            listaOriginal = lista.filter { pilar ->
                pilar.status == StatusPilar.CONCLUIDO ||
                        pilar.status == StatusPilar.EXCLUIDO ||
                        pilar.status == StatusPilar.VENCIDO
            }

            val anosSet = listaOriginal.mapNotNull {
                extrairAno(it.dataPrazo) ?: extrairAno(it.dataExclusao)
            }.toSet()

            val listaAnos = anosSet.sortedDescending().map { it.toString() }.toMutableList()
            if (!listaAnos.contains("Todos")) {
                listaAnos.add(0, "Todos")
            }

            val adapterAno = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item_white,
                listaAnos
            )
            spinnerAnoFiltro.setAdapter(adapterAno)
            spinnerAnoFiltro.setText("Todos", false)

            filtroAnoSelecionado = "Todos"
            filtroStatusSelecionado = "Todos"

            aplicarFiltros()
        }

        spinnerStatusFilter.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) spinnerStatusFilter.showDropDown()
        }
        spinnerStatusFilter.setOnClickListener {
            spinnerStatusFilter.showDropDown()
        }

        spinnerAnoFiltro.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) spinnerAnoFiltro.showDropDown()
        }
        spinnerAnoFiltro.setOnClickListener {
            spinnerAnoFiltro.showDropDown()
        }

        spinnerStatusFilter.setOnItemClickListener { parent, _, position, _ ->
            filtroStatusSelecionado = parent.getItemAtPosition(position).toString()
            aplicarFiltros()
        }

        spinnerAnoFiltro.setOnItemClickListener { parent, _, position, _ ->
            filtroAnoSelecionado = parent.getItemAtPosition(position).toString()
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        val filtrada = listaOriginal.filter { pilar ->
            val statusOk = filtroStatusSelecionado == "Todos" || pilar.status.name == filtroStatusSelecionado.uppercase()
            val anoPilar = extrairAno(pilar.dataPrazo) ?: extrairAno(pilar.dataExclusao)
            val anoOk = filtroAnoSelecionado == "Todos" || anoPilar?.toString() == filtroAnoSelecionado

            statusOk && anoOk
        }
        adapter.atualizarLista(filtrada)
    }

    private fun extrairAno(data: Date?): Int? {
        data ?: return null
        val cal = Calendar.getInstance()
        cal.time = data
        return cal.get(Calendar.YEAR)
    }

    private fun abrirTelaPilar(pilar: PilarEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            val temSubpilares = pilarViewModel.temSubpilares(pilar.id)

            val fragment = if (temSubpilares) {
                TelaPilarComSubpilaresFragment().apply {
                    arguments = Bundle().apply {
                        putInt("pilarId", pilar.id)
                    }
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
