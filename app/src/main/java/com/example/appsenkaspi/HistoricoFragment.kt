package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.*

class HistoricoFragment : Fragment() {

    private lateinit var adapter: TelaHistoricoAdapter
    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private var funcionarioLogadoId: Int = -1
    private var listaOriginal: List<PilarEntity> = emptyList()

    private var textoFiltroAtivo: TextView? = null
    private var filtroStatusSelecionado: String? = null
    private var filtroAnoSelecionado: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerFiltroExclusao)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Correção aqui: os 3 parâmetros do adapter
        adapter = TelaHistoricoAdapter(
            emptyList(),
            { pilar -> abrirTelaPilar(pilar) },
            pilarViewModel
        )
        recyclerView.adapter = adapter

        val spinnerStatusFilter = view.findViewById<Spinner>(R.id.spinnerStatusFilter)
        val spinnerAnoFiltro = view.findViewById<Spinner>(R.id.spinnerStatusFiltro)
        textoFiltroAtivo = view.findViewById(R.id.textoFiltroAtivo)

        val itensStatus = resources.getStringArray(R.array.status_pilar_array)
        val itensAno = resources.getStringArray(R.array.ano_pilar_array)

        val adapterStatus = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itensStatus
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val adapterAno = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itensAno
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerStatusFilter.adapter = adapterStatus
        spinnerAnoFiltro.adapter = adapterAno

        spinnerStatusFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                filtroStatusSelecionado = parent?.getItemAtPosition(position)?.toString()
                aplicarFiltros()
                atualizarTextoFiltro()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                filtroStatusSelecionado = null
                aplicarFiltros()
                atualizarTextoFiltro()
            }
        }

        spinnerAnoFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                filtroAnoSelecionado = parent?.getItemAtPosition(position)?.toString()
                aplicarFiltros()
                atualizarTextoFiltro()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                filtroAnoSelecionado = null
                aplicarFiltros()
                atualizarTextoFiltro()
            }
        }

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
            aplicarFiltros()
        }
    }

    private fun aplicarFiltros() {
        val filtrados = listaOriginal.filter { pilar ->
            val statusOk = filtroStatusSelecionado == null ||
                    filtroStatusSelecionado.equals("TODOS", ignoreCase = true) ||
                    pilar.status.name.equals(filtroStatusSelecionado, ignoreCase = true)

            val ano = extrairAno(pilar.dataPrazo)?.toString()
            val anoOk = filtroAnoSelecionado == null ||
                    filtroAnoSelecionado.equals("TODOS", ignoreCase = true) ||
                    ano == filtroAnoSelecionado

            statusOk && anoOk
        }
        adapter.atualizarLista(filtrados)
    }

    private fun extrairAno(data: Date?): Int? {
        if (data == null) return null
        val cal = Calendar.getInstance()
        cal.time = data
        return cal.get(Calendar.YEAR)
    }

    private fun atualizarTextoFiltro() {
        val status = filtroStatusSelecionado ?: "TODOS"
        val ano = filtroAnoSelecionado ?: "TODOS"
        textoFiltroAtivo?.text = "Status: $status | Ano: $ano"
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
