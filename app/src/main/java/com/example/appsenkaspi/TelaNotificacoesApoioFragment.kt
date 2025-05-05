package com.example.appsenkaspi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.databinding.FragmentTelaNotificacoesApoioBinding

class TelaNotificacoesApoioFragment : Fragment() {

    private lateinit var binding: FragmentTelaNotificacoesApoioBinding
    private lateinit var viewModel: RequisicaoViewModel
    private lateinit var usuarioAtual: FuncionarioEntity // Apoio logado
    private lateinit var funcionarioDao: FuncionarioDao
    private lateinit var pilarDao: PilarDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppDatabase.getDatabase(requireContext())
        funcionarioDao = db.funcionarioDao()
        pilarDao = db.pilarDao()

        binding = FragmentTelaNotificacoesApoioBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RequisicaoViewModel::class.java]

        configurarRecycler()
        return binding.root
    }

    private fun configurarRecycler() {
        viewModel.listarPorSolicitante(usuarioAtual.id).observe(viewLifecycleOwner) { lista ->
            val respostas = lista.filter { it.status != StatusRequisicao.PENDENTE }
            binding.recyclerRespostas.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RespostaRequisicaoAdapter(respostas)
            }
        }
    }
}
