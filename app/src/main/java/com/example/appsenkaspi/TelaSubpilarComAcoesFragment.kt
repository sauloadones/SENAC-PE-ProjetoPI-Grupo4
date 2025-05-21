package com.example.appsenkaspi

import android.animation.ObjectAnimator
import android.graphics.Paint
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

import com.example.appsenkaspi.databinding.FragmentTelaSubpilarBinding
import java.text.SimpleDateFormat
import java.util.*

class TelaSubpilarComAcoesFragment : Fragment() {

  private var _binding: FragmentTelaSubpilarBinding? = null
  private val binding get() = _binding!!

  private val subpilarViewModel: SubpilarViewModel by activityViewModels()
  private val acaoViewModel: AcaoViewModel by activityViewModels()
  private val funcionarioViewModel: FuncionarioViewModel by activityViewModels()
  private val notificacaoViewModel: NotificacaoViewModel by activityViewModels()

  private var subpilarId: Int = -1
  private lateinit var acaoAdapter: AcaoAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    subpilarId = arguments?.getInt("subpilarId") ?: -1
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentTelaSubpilarBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (subpilarId == -1) {
      Toast.makeText(requireContext(), "Subpilar inválido!", Toast.LENGTH_SHORT).show()
      parentFragmentManager.popBackStack()
      return
    }

    configurarBotaoVoltar(view)
    configurarRecycler()
    configurarBotoes()
    observarAcoes()

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

        when (it.cargo) {
          Cargo.APOIO -> {
            binding.cardEditarSubpilar.visibility = View.GONE
            binding.cardAdicionarAcoes.visibility = View.VISIBLE
          }
          Cargo.COORDENADOR -> {
            binding.cardEditarSubpilar.visibility = View.VISIBLE
            binding.cardAdicionarAcoes.visibility = View.VISIBLE
          }
          Cargo.GESTOR -> {
            binding.cardEditarSubpilar.visibility = View.GONE
            binding.cardAdicionarAcoes.visibility = View.GONE
          }

          else -> {
          binding.cardEditarSubpilar.visibility = View.GONE
          binding.cardAdicionarAcoes.visibility = View.GONE
        }
        }
      }
    }

    subpilarViewModel.getSubpilarById(subpilarId).observe(viewLifecycleOwner) { subpilar ->
      if (subpilar != null) {
        preencherCamposComSubpilar(subpilar)
        subpilarViewModel.calcularProgressoDoSubpilar(subpilar.id) { progresso ->
          animarProgresso((progresso * 100).toInt())
        }
      } else {
        Toast.makeText(requireContext(), "Subpilar não encontrado!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
      }
    }

    binding.iconeMenu.setOnClickListener { toggleSobre() }
  }

  private fun configurarRecycler() {
    binding.recyclerAcoes.layoutManager = LinearLayoutManager(requireContext())
    acaoAdapter = AcaoAdapter { acao -> abrirTelaAcao(acao) }
    binding.recyclerAcoes.adapter = acaoAdapter
  }

  private fun observarAcoes() {
    val recycler = binding.recyclerAcoes
    val emptyView = binding.emptyStateView

    acaoViewModel.listarAcoesPorSubpilar(subpilarId).observe(viewLifecycleOwner) { lista ->
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

  private fun preencherCamposComSubpilar(subpilar: SubpilarEntity) {
    binding.tituloSubpilar.text = "${subpilar.id}° Subpilar"
    binding.subtituloSubpilar.apply {
      text = subpilar.nome.ifBlank { "Sem nome" }
      paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    binding.dataPrazoSubpilar.text = "Prazo: ${sdf.format(subpilar.dataPrazo)}"
    binding.textoSobre.text = if (subpilar.descricao.isNullOrBlank()) {
      "Nenhuma descrição adicionada."
    } else {
      subpilar.descricao
    }
  }

  private fun configurarBotoes() {
    binding.cardEditarSubpilar.setOnClickListener {
      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, EditarSubpilarFragment().apply {
          arguments = Bundle().apply { putInt("subpilarId", subpilarId) }
        })
        .addToBackStack(null)
        .commit()
    }

    binding.cardAdicionarAcoes.setOnClickListener {
      parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, CriarAcaoFragment().apply {
          arguments = Bundle().apply { putInt("subpilarId", subpilarId) }
        })
        .addToBackStack(null)
        .commit()
    }
  }

  private fun abrirTelaAcao(acao: AcaoEntity) {
    val fragment = TelaAcaoFragment().apply {
      arguments = Bundle().apply { putInt("acaoId", acao.id!!) }
    }
    parentFragmentManager.beginTransaction()
      .replace(R.id.main_container, fragment)
      .addToBackStack(null)
      .commit()
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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  companion object {
    fun newInstance(subpilarId: Int): TelaSubpilarComAcoesFragment {
      val fragment = TelaSubpilarComAcoesFragment()
      fragment.arguments = Bundle().apply {
        putInt("subpilarId", subpilarId)
      }
      return fragment
    }
  }
}
