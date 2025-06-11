package com.example.appsenkaspi.ui.perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.R
import com.example.appsenkaspi.databinding.FragmentTrabalhosBinding
import com.example.appsenkaspi.ui.atividade.TelaAtividadeFragment
import com.example.appsenkaspi.viewmodel.AtividadeViewModel

class TrabalhosFragment : Fragment() {

  private var _binding: FragmentTrabalhosBinding? = null
  private val binding get() = _binding!!

  private val atividadeViewModel: AtividadeViewModel by activityViewModels()

  private lateinit var adapter: AtividadePerfilAdapter

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    _binding = FragmentTrabalhosBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val prefs = requireContext().getSharedPreferences("funcionario_prefs", Context.MODE_PRIVATE)
    val funcionarioId = prefs.getInt("funcionario_id", -1)

    if (funcionarioId == -1) {
      Toast.makeText(requireContext(), "Funcionário não identificado!", Toast.LENGTH_SHORT).show()
      return
    }

    configurarRecycler()

    atividadeViewModel
      .listarAtividadesComFuncionariosPorFuncionario(funcionarioId)
      .observe(viewLifecycleOwner) { atividades ->
        if (atividades.isNullOrEmpty()) {
          binding.recyclerAtividades.visibility = View.GONE
          binding.emptyStateView.visibility = View.VISIBLE
        } else {
          binding.recyclerAtividades.visibility = View.VISIBLE
          binding.emptyStateView.visibility = View.GONE
          adapter.submitList(atividades)
        }
      }
  }

  private fun configurarRecycler() {
    adapter = AtividadePerfilAdapter { atividadeComFuncionarios ->
      val fragment = TelaAtividadeFragment().apply {
        arguments = Bundle().apply {
          putInt("atividadeId", atividadeComFuncionarios.atividade.id!!)
        }
      }
      requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.main_container, fragment)
        .addToBackStack(null)
        .commit()
    }

      binding.recyclerAtividades.layoutManager = LinearLayoutManager(requireContext())
    binding.recyclerAtividades.adapter = adapter
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
