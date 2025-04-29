package com.example.appsenkaspi

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelecionarFuncionariosDialogFragment : DialogFragment() {

    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val selecionados = mutableListOf<FuncionarioEntity>()
    private lateinit var adapter: FuncionarioMultiSelecaoAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.popup_selecionar_funcionarios, null)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewDialogFuncionarios)
        val btnConfirm = view.findViewById<Button>(R.id.buttonConfirmarSelecao)

        // configura RecyclerView
        adapter = FuncionarioMultiSelecaoAdapter(selecionados)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // carrega lista e preenche adapter
        funcionarioViewModel.listaFuncionarios.observe(this) { lista ->
            adapter.submitList(lista)
        }

        btnConfirm.setOnClickListener {
            // envia resultado para o CriarAcaoFragment
            parentFragmentManager.setFragmentResult(
                "funcionariosSelecionados",
                bundleOf("listaFuncionarios" to ArrayList(selecionados))
            )
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}
