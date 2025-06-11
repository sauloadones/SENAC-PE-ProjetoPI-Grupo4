package com.example.appsenkaspi.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.data.local.entity.FuncionarioEntity
import com.example.appsenkaspi.databinding.DialogEscolherResponsaveisBinding
import com.example.appsenkaspi.ui.responsaveis.ResponsavelAdapter
import com.example.appsenkaspi.viewmodel.FuncionarioViewModel

class SelecionarResponsavelDialogFragment : DialogFragment() {

    private var _binding: DialogEscolherResponsaveisBinding? = null
    private val binding get() = _binding!!

    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val selecionados = mutableListOf<FuncionarioEntity>()
    private lateinit var adapter: ResponsavelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogEscolherResponsaveisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Inicialize o adapter primeiro
        adapter = ResponsavelAdapter(
            funcionarios = listOf(), // inicial vazio
            selecionados = selecionados,
            onSelecionadosAtualizados = { listaAtualizada ->
                // Aqui você pode atualizar UI ou estado se quiser
            }
        )

        binding.recyclerMembros.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMembros.adapter = adapter

        // ✅ Agora é seguro observar e usar o adapter
        funcionarioViewModel.listasFuncionarios.observe(viewLifecycleOwner) { lista ->
            adapter.atualizarLista(lista)
        }

        binding.fecharDialog.setOnClickListener {
            dismiss()
        }

        binding.buttonConfirmarSelecionados.setOnClickListener {
            val result = Bundle().apply {
                putParcelableArrayList("listaFuncionarios", ArrayList(selecionados))
            }
            parentFragmentManager.setFragmentResult("funcionariosSelecionados", result)
            dismiss()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
