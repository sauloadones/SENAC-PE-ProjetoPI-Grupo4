package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.databinding.FragmentTelaNotificacoesCoordenadorBinding

class TelaNotificacoesCoordenadorFragment : Fragment() {

    private lateinit var binding: FragmentTelaNotificacoesCoordenadorBinding
    private lateinit var viewModel: RequisicaoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTelaNotificacoesCoordenadorBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RequisicaoViewModel::class.java]

        configurarRecyclerView()
        return binding.root
    }

    private fun configurarRecyclerView() {
        viewModel.listarPorStatus(StatusRequisicao.PENDENTE).observe(viewLifecycleOwner) { lista ->
            binding.recyclerRequisicoes.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RequisicaoAdapter(lista) { requisicao ->
                    val fragment = TelaDetalheRequisicaoFragment().apply {
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
        }
    }
}
