package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoricoFragment : Fragment() {

    private lateinit var viewModel: PilarViewModel

    private lateinit var recyclerConcluidos: RecyclerView
    private lateinit var recyclerExcluidos: RecyclerView
    private lateinit var recyclerVencidos: RecyclerView

    private lateinit var adapterConcluidos: PilarHistoricoAdapter
    private lateinit var adapterExcluidos: PilarHistoricoAdapter
    private lateinit var adapterVencidos: PilarHistoricoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historico, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(PilarViewModel::class.java)

        recyclerConcluidos = view.findViewById(R.id.recyclerConcluidos)
        recyclerExcluidos = view.findViewById(R.id.recyclerExcluidos)
        recyclerVencidos = view.findViewById(R.id.recyclerVencidos)

        adapterConcluidos = PilarHistoricoAdapter(emptyList())
        adapterExcluidos = PilarHistoricoAdapter(emptyList())
        adapterVencidos = PilarHistoricoAdapter(emptyList())

        recyclerConcluidos.layoutManager = LinearLayoutManager(requireContext())
        recyclerExcluidos.layoutManager = LinearLayoutManager(requireContext())
        recyclerVencidos.layoutManager = LinearLayoutManager(requireContext())

        recyclerConcluidos.adapter = adapterConcluidos
        recyclerExcluidos.adapter = adapterExcluidos
        recyclerVencidos.adapter = adapterVencidos

        val layoutConcluidos = view.findViewById<View>(R.id.layoutConcluidos)
        val layoutExcluidos = view.findViewById<View>(R.id.layoutExcluidos)
        val layoutVencidos = view.findViewById<View>(R.id.layoutVencidos)

        viewModel.pilaresConcluidos.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                layoutConcluidos.visibility = View.VISIBLE
                adapterConcluidos.atualizarLista(it)
            } else {
                layoutConcluidos.visibility = View.GONE
            }
        }

        viewModel.pilaresExcluidos.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                layoutExcluidos.visibility = View.VISIBLE
                adapterExcluidos.atualizarLista(it)
            } else {
                layoutExcluidos.visibility = View.GONE
            }
        }

        viewModel.pilaresVencidos.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                layoutVencidos.visibility = View.VISIBLE
                adapterVencidos.atualizarLista(it)
            } else {
                layoutVencidos.visibility = View.GONE
            }
        }
    }
}
