package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.appsenkaspi.databinding.FragmentFiltroStatusBinding

class BottomSheetFiltroStatusFragment(
  private val statusSelecionado: StatusPilar?,
  private val onStatusSelecionado: (StatusPilar?) -> Unit
) : BottomSheetDialogFragment() {

  private var _binding: FragmentFiltroStatusBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentFiltroStatusBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.opcaoTodos.isChecked = statusSelecionado == null
    binding.opcaoConcluido.isChecked = statusSelecionado == StatusPilar.CONCLUIDO
    binding.opcaoVencido.isChecked = statusSelecionado == StatusPilar.VENCIDO
    binding.opcaoDeletado.isChecked = statusSelecionado == StatusPilar.EXCLUIDO

    binding.opcaoTodos.setOnClickListener {
      onStatusSelecionado(null)
      dismiss()
    }

    binding.opcaoConcluido.setOnClickListener {
      onStatusSelecionado(StatusPilar.CONCLUIDO)
      dismiss()
    }

    binding.opcaoVencido.setOnClickListener {
      onStatusSelecionado(StatusPilar.VENCIDO)
      dismiss()
    }

    binding.opcaoDeletado.setOnClickListener {
      onStatusSelecionado(StatusPilar.EXCLUIDO)
      dismiss()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
