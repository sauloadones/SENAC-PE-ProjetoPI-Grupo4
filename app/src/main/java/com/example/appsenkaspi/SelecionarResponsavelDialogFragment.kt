package com.example.appsenkaspi

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.databinding.DialogEscolherResponsaveisBinding

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

        adapter = ResponsavelAdapter(emptyList(), selecionados) { atualizados ->
            // Atualização opcional em tempo real se quiser exibir contadores
        }

        binding.recyclerMembros.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMembros.adapter = adapter

        funcionarioViewModel.listasFuncionarios.observe(viewLifecycleOwner) { lista ->
            adapter = ResponsavelAdapter(lista, selecionados) { atualizados ->
                // Atualiza a lista sempre que houver clique
            }
            binding.recyclerMembros.adapter = adapter
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
