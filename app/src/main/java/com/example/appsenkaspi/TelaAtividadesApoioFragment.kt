package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.Converters.Cargo
import com.example.appsenkaspi.databinding.FragmentTelaAtividadesApoioBinding
import com.example.appsenkaspi.utils.configurarBotaoVoltar

class TelaAtividadesApoioFragment : Fragment() {

    private var _binding: FragmentTelaAtividadesApoioBinding? = null
    private val binding get() = _binding!!

    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val atividadeViewModel: AtividadeViewModel by activityViewModels()

    private lateinit var adapter: AtividadeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaAtividadesApoioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)

        adapter = AtividadeAdapter { atividadeComFuncionarios ->
            val fragment = TelaAtividadeFragment().apply {
                arguments = Bundle().apply {
                    putInt("atividadeId", atividadeComFuncionarios.atividade.id)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerMinhasAtividades.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMinhasAtividades.adapter = adapter

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            if (funcionario == null) {
                Toast.makeText(requireContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
                return@observe
            }

            if (funcionario.cargo != Cargo.APOIO) {
                Toast.makeText(requireContext(), "Acesso permitido apenas ao Apoio", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
                return@observe
            }

            carregarAtividadesDoApoio(funcionario.id)
        }
    }

    private fun carregarAtividadesDoApoio(funcionarioId: Int) {
        atividadeViewModel.getAtividadesDoFuncionarioComResponsaveis(funcionarioId)
            .observe(viewLifecycleOwner) { lista ->
                if (lista.isNullOrEmpty()) {
                    binding.emptyStateView.visibility = View.VISIBLE
                    binding.recyclerMinhasAtividades.visibility = View.GONE
                } else {
                    binding.emptyStateView.visibility = View.GONE
                    binding.recyclerMinhasAtividades.visibility = View.VISIBLE
                    adapter.submitList(lista)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
