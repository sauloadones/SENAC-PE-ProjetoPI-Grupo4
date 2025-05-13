package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.graphics.Paint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.appsenkaspi.databinding.FragmentTelaPilarBinding
import java.text.SimpleDateFormat
import java.util.*

class TelaPilarFragment : Fragment() {

    private var _binding: FragmentTelaPilarBinding? = null
    private val binding get() = _binding!!

    private val pilarViewModel: PilarViewModel by activityViewModels()
    private val acaoViewModel: AcaoViewModel by activityViewModels()
    private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
    private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

    private var pilarId: Int = -1
    private lateinit var acaoAdapter: AcaoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPilarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotaoVoltar(view)
        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
          funcionario?.let {
            configurarNotificacaoBadge(
              rootView = view,
              lifecycleOwner = viewLifecycleOwner,
              fragmentManager = parentFragmentManager,
              funcionarioId = it.id,
              cargo = it.cargo,
              viewModel = notificacaoViewModel
            )
          }
        }



        funcionarioViewModel.funcionarioLogado.observe(viewLifecycleOwner) { funcionario ->
            when (funcionario?.cargo) {
                Cargo.APOIO -> {
                    binding.cardEditarPilar.visibility = View.GONE
                    binding.cardAdicionarAcoes.visibility = View.VISIBLE
                }

                Cargo.COORDENADOR -> {
                    binding.cardEditarPilar.visibility = View.VISIBLE
                    binding.cardAdicionarAcoes.visibility = View.VISIBLE

                }
                Cargo.GESTOR -> {
                    binding.cardEditarPilar.visibility = View.GONE
                    binding.cardAdicionarAcoes.visibility = View.GONE

                }

                else -> {
                    binding.cardEditarPilar.visibility = View.GONE
                    binding.cardAdicionarAcoes.visibility = View.GONE

                }
            }
        }


        // 1) Recupera o ID
        pilarId = arguments?.getInt("pilarId") ?: -1
        if (pilarId == -1) {
            Toast.makeText(requireContext(), "Pilar inválido!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // 2) OBSERVA o LiveData do ViewModel
        pilarViewModel.getPilarById(pilarId)
            .observe(viewLifecycleOwner) { pilar ->
                if (pilar != null) {
                    preencherCamposComPilar(pilar)
                } else {
                    Toast.makeText(requireContext(), "Pilar não encontrado!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }

        configurarRecycler()
        configurarBotoes()
        binding.iconeMenu.setOnClickListener { toggleSobre() }
        observarAcoes()
    }

    private fun preencherCamposComPilar(pilar: PilarEntity) {
        binding.tituloPilar.text = "${pilar.id}° Pilar"
        binding.subtituloPilar.apply {
            text = pilar.nome.ifBlank { "Sem nome" }
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dataPrazoPilar.text = "Prazo: ${sdf.format(pilar.dataPrazo)}"
        binding.textoSobre.text = pilar.descricao.ifBlank { "Nenhuma descrição adicionada." }

        // 🚀 Chamada para calcular progresso real
        pilarViewModel.calcularProgressoDoPilar(pilar.id) { progresso ->
            animarProgresso((progresso * 100).toInt())
        }
    }


    private fun configurarBotoes() {
        binding.cardEditarPilar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, EditarPilarFragment().apply {
                    arguments = Bundle().apply { putInt("pilarId", pilarId) }
                })
                .addToBackStack(null)
                .commit()
        }
        binding.cardAdicionarAcoes.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, CriarAcaoFragment().apply {
                    arguments = Bundle().apply { putInt("pilarId", pilarId) }
                })
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
        ObjectAnimator.ofInt(binding.progressoPilar, "progress", target).apply {
            duration = 500
            start()
        }
        binding.percentual.text = "$target%"
    }

    private fun configurarRecycler() {
        binding.recyclerAcoes.layoutManager = LinearLayoutManager(requireContext())
        acaoAdapter = AcaoAdapter { acao -> abrirTelaAcao(acao) }
        binding.recyclerAcoes.adapter = acaoAdapter
    }


    private fun observarAcoes() {
        val recycler = binding.recyclerAcoes
        val emptyView = binding.emptyStateView

        acaoViewModel.listarAcoesPorPilar(pilarId).observe(viewLifecycleOwner) { lista ->
            if (lista.isNullOrEmpty()) {
                recycler.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recycler.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                acaoAdapter.submitList(lista)
            }
        }
    }

    private fun abrirTelaAcao(acao: AcaoEntity) {
        val fragment = TelaAcaoFragment().apply {
            arguments = Bundle().apply { putInt("acaoId", acao.id) }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
