package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appsenkaspi.databinding.FragmentTelaAcaoBinding
import com.example.appsenkaspi.utils.configurarBotaoVoltar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TelaAcaoFragment : Fragment() {

    private var _binding: FragmentTelaAcaoBinding? = null
    private val binding get() = _binding!!

    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val atividadeViewModel: AtividadeViewModel by activityViewModels()
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

        acaoId = arguments?.getInt("acaoId") ?: -1
        if (acaoId == -1) {
            Toast.makeText(requireContext(), "Acao Invalida!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        acaoViewModel.getAcaoById(acaoId)
            .observe(viewLifecycleOwner) { acao ->
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
    }

    private fun preencherCamposComAcao(acao: AcaoEntity) {
        binding.tituloAcao.text = acao.nome.ifBlank { "Ação sem nome" }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dataPrazoPilar.text = "Prazo: ${sdf.format(acao.dataPrazo)}"

        binding.textoSobre.text = acao.descricao.ifBlank { "Nenhuma descrição adicionada." }

        animarProgresso(0)
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
                    putInt("atividadeId", atividadeComFuncionarios.atividade.id)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment) // Altere para o ID correto do seu container
                .addToBackStack(null)
                .commit()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = atividadeAdapter

        atividadeViewModel.listarAtividadesComFuncionariosPorAcao(acaoId).observe(viewLifecycleOwner) { atividades ->
            if (atividades.isNullOrEmpty()) {
                recycler.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recycler.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                atividadeAdapter.submitList(atividades)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
