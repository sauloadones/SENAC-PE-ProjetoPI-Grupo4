package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.appsenkaspi.databinding.FragmentTelaAcaoBinding
import java.text.SimpleDateFormat
import java.util.*

class TelaAcaoFragment : Fragment() {

    private var _binding: FragmentTelaAcaoBinding? = null
    private val binding get() = _binding!!

    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val atividadeViewModel: AtividadeViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()

    private var acaoId: Int = -1
    private lateinit var atividadeAdapter: AtividadeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaAcaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)

        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {
                Cargo.APOIO -> {

                    binding.cardEditarAcao.visibility = View.VISIBLE
                    binding.cardAdicionarAtividade.visibility = View.VISIBLE

                }

                Cargo.COORDENADOR -> {
                    binding.cardAdicionarAtividade.visibility = View.VISIBLE

                    binding.cardEditarAcao.visibility = View.VISIBLE


                }
                Cargo.GESTOR -> {
                    binding.cardAdicionarAtividade.visibility = View.GONE

                    binding.cardEditarAcao.visibility = View.GONE




                }

                else -> {
                    binding.cardAdicionarAtividade.visibility = View.GONE

                    binding.cardEditarAcao.visibility = View.GONE

                }
            }
        }

        acaoId = arguments?.getInt("acaoId") ?: -1
        if (acaoId == -1) {
            Toast.makeText(requireContext(), "Acao Invalida!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        acaoViewModel.getAcaoById(acaoId).observe(viewLifecycleOwner) { acao ->
            if (acao != null) {
                preencherCamposComAcao(acao)
            } else {
                Toast.makeText(requireContext(), "Ação não encontrada!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        configurarBotoes()
        binding.iconeMenu.setOnClickListener { toggleSobre() }
        observarAtividades()
        acaoViewModel.atualizarStatusAcaoAutomaticamente(acaoId)

    }

    private fun preencherCamposComAcao(acao: AcaoEntity) {
        binding.tituloAcao.text = acao.nome.ifBlank { "Ação sem nome" }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dataPrazoPilar.text = "Prazo: ${sdf.format(acao.dataPrazo)}"

        binding.textoSobre.text = acao.descricao.ifBlank { "Nenhuma descrição adicionada." }

        // Removido: atualizarProgressoAcao()
        // A atualização agora ocorre automaticamente com base no observer das atividades
    }

    private fun configurarBotoes() {
        binding.cardEditarAcao.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    EditarAcaoFragment().apply {
                        arguments = Bundle().apply { putInt("acaoId", acaoId) }
                    }
                )
                .addToBackStack(null)
                .commit()
        }

        binding.cardAdicionarAtividade.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.main_container,
                    CriarAtividadeFragment().apply {
                        arguments = Bundle().apply { putInt("acaoId", acaoId) }
                    }
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun toggleSobre() {
        val transition = AutoTransition().apply { duration = 300 }
        TransitionManager.beginDelayedTransition(binding.cabecalhoCard, transition)
        if (binding.sobreWrapper.visibility == View.VISIBLE) {
            binding.sobreWrapper.visibility = View.GONE
            binding.iconeMenu.animate().rotation(0f).setDuration(300).start()
            binding.headerLayout.elevation = 8f
        } else {
            binding.sobreWrapper.visibility = View.VISIBLE
            binding.iconeMenu.animate().rotation(180f).setDuration(300).start()
            binding.headerLayout.elevation = 16f
        }
    }

    private fun animarProgresso(target: Int) {
        ObjectAnimator.ofInt(binding.progressoAcao, "progress", target).apply {
            duration = 500
            start()
        }
        binding.percentual.text = "$target%"
    }

    private fun observarAtividades() {
        val recycler = binding.recyclerAtividades
        val emptyView = binding.emptyStateView

        atividadeAdapter = AtividadeAdapter { atividadeComFuncionarios ->
            val fragment = TelaAtividadeFragment().apply {
                arguments = Bundle().apply {
                    putInt("atividadeId", atividadeComFuncionarios.atividade.id!!)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = atividadeAdapter

        atividadeViewModel.listarAtividadesComFuncionariosPorAcao(acaoId)
            .observe(viewLifecycleOwner) { atividades ->
                if (atividades.isNullOrEmpty()) {
                    recycler.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    animarProgresso(0)
                } else {
                    recycler.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                    atividadeAdapter.submitList(atividades)

                    // 🎯 Calcular progresso da barra da tela
                    val total = atividades.size
                    val concluidas = atividades.count { it.atividade.status.name == "CONCLUIDA" }
                    val progresso = if (total > 0) (concluidas * 100 / total) else 0
                    animarProgresso(progresso)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
